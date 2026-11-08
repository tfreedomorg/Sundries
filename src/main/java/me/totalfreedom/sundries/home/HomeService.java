package me.totalfreedom.sundries.home;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.config.ConfigEntry;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.sundries.sql.SundriesDatabase;
import me.totalfreedom.sundries.teleport.StoredLocation;
import me.totalfreedom.sundries.util.FLog;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.sql.PersistenceQueue;

import reactor.core.publisher.Mono;

public class HomeService extends FreedomService
{
    private static final long SAVE_TIMEOUT_MILLIS = 5000L;

    private final MainConfig config;
    private final HomeRepository repository;
    private final PersistenceQueue writes = new PersistenceQueue("home");
    private final Map<UUID, Map<String, Home>> homes = new HashMap<>();

    public HomeService(FreedomAPI plugin, MainConfig config, SundriesDatabase database)
    {
        super(plugin);
        this.config = config;
        this.repository = new HomeRepository(database);
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
            FLog.error("Could not create the homes table, so homes won't be saved.");
            FLog.error(ex);
        }
        // Only matters on a reload, since it cleared the homes of everyone still online
        server.getOnlinePlayers().forEach(player -> loadHomes(player.getUniqueId()));
    }

    @Override
    public void onStop()
    {
        writes.await(SAVE_TIMEOUT_MILLIS);
        homes.clear();
    }

    public List<String> getHomeNames(UUID uuid)
    {
        return homes.getOrDefault(uuid, Map.of())
                    .values()
                    .stream()
                    .map(Home::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
    }

    /**
     * Looks up {@code uuid}'s homes, then hands them to {@code callback} on the main thread. An
     * online player's come from memory and anyone else's from the database.
     */
    public void findHomes(UUID uuid, Consumer<List<Home>> callback)
    {
        final Map<String, Home> loaded = homes.get(uuid);

        if (loaded != null)
        {
            callback.accept(List.copyOf(loaded.values()));
            return;
        }

        plugin.database().readAsync("Sundries/findHomes", repository.loadHomes(uuid), callback,
                () -> callback.accept(List.of()));
    }

    public void findHome(UUID uuid, String name, Consumer<Optional<Home>> callback)
    {
        findHomes(uuid, found -> callback.accept(found.stream()
                                                      .filter(home -> home.name().equalsIgnoreCase(name))
                                                      .findFirst()));
    }

    /**
     * Saves a home, replacing any with the same name in any case. The new one keeps the case
     * {@code name} was typed in.
     * 
     * @return False if this would be a new home and {@code uuid} is already at the limit.
     */
    public boolean setHome(UUID uuid, String name, Location location)
    {
        final String key = normalize(name);
        final Map<String, Home> playerHomes = homes.computeIfAbsent(uuid, ignored -> new HashMap<>());

        if (!playerHomes.containsKey(key) && playerHomes.size() >= config.getInteger(ConfigEntry.HOMES_MAX_PER_PLAYER))
            return false;

        final Home home = new Home(name, StoredLocation.of(location));
        playerHomes.put(key, home);
        enqueue(repository.saveHome(uuid, home), uuid);
        return true;
    }

    /**
     * Deletes a home, then hands {@code callback} the deleted home on the main thread, or empty if
     * {@code uuid} had none with that name.
     */
    public void deleteHome(UUID uuid, String name, Consumer<Optional<Home>> callback)
    {
        findHome(uuid, name, found ->
        {
            found.ifPresent(home ->
            {
                homes.getOrDefault(uuid, new HashMap<>()).remove(normalize(name));
                enqueue(repository.deleteHome(uuid, home.name()), uuid);
            });

            callback.accept(found);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        loadHomes(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        homes.remove(event.getPlayer().getUniqueId());
    }

    private void loadHomes(UUID uuid)
    {
        // putIfAbsent, because a home set while this loads is newer than the stored row
        plugin.database().readAsync("Sundries/loadHomes", repository.loadHomes(uuid),
                loaded ->
                {
                    if (server.getPlayer(uuid) == null)
                        return;

                    final Map<String, Home> playerHomes = homes.computeIfAbsent(uuid, ignored -> new HashMap<>());
                    loaded.forEach(home -> playerHomes.putIfAbsent(normalize(home.name()), home));
                },
                () -> {});
    }

    private void enqueue(Mono<Void> write, UUID uuid)
    {
        writes.enqueue(write.onErrorResume(ex ->
        {
            FLog.error("Could not save a home change for %s: %s", uuid, ex.getMessage());
            return Mono.empty();
        }));
    }

    private static String normalize(String name)
    {
        return name.toLowerCase(Locale.ROOT);
    }
}