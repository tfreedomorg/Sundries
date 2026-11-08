package me.totalfreedom.sundries.warp;

import java.sql.SQLException;
import java.util.*;

import org.bukkit.Location;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.sql.SundriesDatabase;
import me.totalfreedom.sundries.teleport.StoredLocation;
import me.totalfreedom.sundries.util.FLog;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.sql.PersistenceQueue;

import reactor.core.publisher.Mono;

public class WarpService extends FreedomService
{
    private static final long SAVE_TIMEOUT_MILLIS = 5000L;

    private final WarpRepository repository;
    private final PersistenceQueue writes = new PersistenceQueue("warp");
    private final Map<String, Warp> warps = new HashMap<>();

    public WarpService(FreedomAPI plugin, SundriesDatabase database)
    {
        super(plugin);
        this.repository = new WarpRepository(database);
    }

    @Override
    public void onStart()
    {
        try
        {
            repository.createTable();
        }
        catch (SQLException ex)
        {
            FLog.error("Could not create the warps table, so warps won't be saved.");
            FLog.error(ex);
        }

        // putIfAbsent, because a warp set while this loads is newer than the stored row
        plugin.database().readAsync("Sundries/loadWarps", repository.loadWarps(),
                loaded -> loaded.forEach(warp -> warps.putIfAbsent(normalize(warp.name()), warp)),
                () -> {});
    }

    @Override
    public void onStop()
    {
        writes.await(SAVE_TIMEOUT_MILLIS);
        warps.clear();
    }

    public Collection<Warp> getWarps()
    {
        return List.copyOf(warps.values());
    }

    public List<String> getWarpNames()
    {
        return warps.values()
                    .stream()
                    .map(Warp::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
    }

    public Optional<Warp> getWarp(String name)
    {
        return Optional.ofNullable(warps.get(normalize(name)));
    }

    public List<String> getWarpNamesBy(UUID creator)
    {
        return warps.values()
                    .stream()
                    .filter(warp -> warp.creator().equals(creator))
                    .map(Warp::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
    }

    /**
     * Saves a warp at {@code location}. Moving an existing warp keeps its original creator, so
     * an admin moving it doesn't take it over, but takes the case {@code name} was typed in.
     */
    public void setWarp(String name, UUID creator, Location location)
    {
        final String key = normalize(name);
        final UUID owner = getWarp(key).map(Warp::creator)
                                       .orElse(creator);
        final Warp warp = new Warp(name, owner, StoredLocation.of(location));

        warps.put(key, warp);
        enqueue(repository.saveWarp(warp), key);
    }

    public void deleteWarp(String name)
    {
        final String key = normalize(name);

        if (warps.remove(key) != null)
            enqueue(repository.deleteWarp(name), key);
    }

    private void enqueue(Mono<Void> write, String name)
    {
        writes.enqueue(write.onErrorResume(ex ->
        {
            FLog.error("Could not save a change to warp %s: %s", name, ex.getMessage());
            return Mono.empty();
        }));
    }

    private static String normalize(String name)
    {
        return name.toLowerCase(Locale.ROOT);
    }
}