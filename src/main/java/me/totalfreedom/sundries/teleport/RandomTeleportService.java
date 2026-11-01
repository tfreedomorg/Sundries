package me.totalfreedom.sundries.teleport;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.config.ConfigEntry;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.totalfreedommod.FreedomService;

public class RandomTeleportService extends FreedomService
{
    // The Nether's highest block is its bedrock roof, so the search starts inside the caves instead
    private static final int NETHER_SEARCH_HEIGHT = 64;

    private final MainConfig config;
    private final TeleportService teleports;
    private final Map<UUID, Long> cooldownEnds = new HashMap<>();

    public RandomTeleportService(FreedomAPI plugin, MainConfig config, TeleportService teleports)
    {
        super(plugin);
        this.config = config;
        this.teleports = teleports;
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop()
    {
    }

    /**
     * @return How many seconds {@code uuid} must still wait, rounded up, or 0 if they can teleport now.
     */
    public long getRemainingCooldown(UUID uuid)
    {
        final long remainingMillis = cooldownEnds.getOrDefault(uuid, 0L) - System.currentTimeMillis();
        return remainingMillis <= 0 ? 0 : TimeUnit.MILLISECONDS.toSeconds(remainingMillis + 999);
    }

    /**
     * Starts {@code player}'s cooldown and teleports them to a random safe spot in their world.
     * 
     * @return A future holding whether the teleport happened. It is false if no attempt found a safe spot.
     */
    public CompletableFuture<Boolean> teleport(Player player)
    {
        final long cooldownMillis = TimeUnit.SECONDS.toMillis(config.getInteger(ConfigEntry.TELEPORT_RANDOM_COOLDOWN_SECONDS));
        cooldownEnds.put(player.getUniqueId(), System.currentTimeMillis() + cooldownMillis);

        return attempt(player, config.getInteger(ConfigEntry.TELEPORT_RANDOM_MAX_ATTEMPTS));
    }

    private CompletableFuture<Boolean> attempt(Player player, int attemptsLeft)
    {
        if (attemptsLeft <= 0)
            return CompletableFuture.completedFuture(false);

        final World world = player.getWorld();
        final Location candidate = randomLocation(world);

        if (!world.getWorldBorder().isInside(candidate))
            return attempt(player, attemptsLeft - 1);

        return world.getChunkAtAsync(candidate).thenCompose(chunk ->
        {
            candidate.setY(world.getEnvironment() == World.Environment.NETHER
                    ? NETHER_SEARCH_HEIGHT
                    : world.getHighestBlockYAt(candidate) + 1);

            final List<String> excludedBiomes = config.getStringList(ConfigEntry.TELEPORT_RANDOM_EXCLUDED_BIOMES);
            final Optional<Location> landing = TeleportService.findSafeLanding(candidate)
                                                              .filter(spot -> !excludedBiomes.contains(world.getBiome(spot).key().value()));

            // The landing has already been checked, so the teleport doesn't need to check it again
            return landing.isPresent()
                    ? teleports.teleport(player, landing.get(), true)
                    : attempt(player, attemptsLeft - 1);
        });
    }

    private Location randomLocation(World world)
    {
        final int minRange = config.getInteger(ConfigEntry.TELEPORT_RANDOM_MIN_RANGE);
        final int maxRange = config.getInteger(ConfigEntry.TELEPORT_RANDOM_MAX_RANGE);
        final double distance = minRange + ThreadLocalRandom.current().nextDouble() * (maxRange - minRange);
        final double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;

        return world.getSpawnLocation().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
    }
}