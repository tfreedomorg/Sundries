package me.totalfreedom.sundries.teleport;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.config.ConfigEntry;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.totalfreedommod.FreedomService;

public class TeleportService extends FreedomService
{
    private static final Set<Material> UNSAFE_SPACE = Set.of(
            Material.LAVA, Material.FIRE, Material.SOUL_FIRE, Material.SWEET_BERRY_BUSH, Material.COBWEB,
            Material.POWDER_SNOW, Material.WITHER_ROSE, Material.NETHER_PORTAL,
            Material.END_PORTAL, Material.END_GATEWAY);
    private static final Set<Material> UNSAFE_GROUND = Set.of(
            Material.CACTUS, Material.MAGMA_BLOCK, Material.CAMPFIRE, Material.SOUL_CAMPFIRE);

    private final MainConfig config;
    private final Map<UUID, Long> invulnerableUntil = new HashMap<>();

    public TeleportService(FreedomAPI plugin, MainConfig config)
    {
        super(plugin);
        this.config = config;
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop()
    {
        invulnerableUntil.clear();
    }

    /**
     * Teleports {@code player} to {@code destination}, centered on the block. Unless
     * {@code ignoreSafety} is set, or the player is flying or in creative or spectator, they land
     * on the nearest safe spot in that column instead.
     * 
     * @return A future holding whether the teleport happened. It is false if the column has no
     * safe spot or the teleport was cancelled.
     */
    public CompletableFuture<Boolean> teleport(Player player, Location destination, boolean ignoreSafety)
    {
        return destination.getWorld().getChunkAtAsync(destination).thenCompose(chunk ->
        {
            final Optional<Location> landing = ignoreSafety || isExemptFromSafety(player)
                    ? Optional.of(centered(destination))
                    : findSafeLanding(destination);

            if (landing.isEmpty())
                return CompletableFuture.completedFuture(false);

            player.eject();
            final long invulnerableMillis = TimeUnit.SECONDS.toMillis(config.getInteger(ConfigEntry.TELEPORT_INVULNERABILITY_SECONDS));
            invulnerableUntil.put(player.getUniqueId(), System.currentTimeMillis() + invulnerableMillis);
            return player.teleportAsync(landing.get(), TeleportCause.COMMAND);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && isInvulnerable(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        invulnerableUntil.remove(event.getPlayer().getUniqueId());
    }

    private boolean isInvulnerable(Player player)
    {
        return invulnerableUntil.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    private static boolean isExemptFromSafety(Player player)
    {
        return player.isFlying()
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR;
    }

    /**
     * Searches outward from the destination's height, so the closest safe spot wins whether it's
     * above or below.
     */
    private static Optional<Location> findSafeLanding(Location destination)
    {
        final World world = destination.getWorld();
        final int startY = destination.getBlockY();

        return IntStream.range(world.getMinHeight(), world.getMaxHeight() - 1)
                        .boxed()
                        .sorted(Comparator.comparingInt((Integer y) -> Math.abs(y-startY)))
                        .map(y -> world.getBlockAt(destination.getBlockX(), y, destination.getBlockZ()))
                        .filter(TeleportService::isSafeToStandIn)
                        .findFirst()
                        .map(feet -> withHeight(centered(destination), feet.getY()));
    }

    private static boolean isSafeToStandIn(Block feet)
    {
        final Block ground = feet.getRelative(BlockFace.DOWN);

        return isClearSpace(feet)
                && isClearSpace(feet.getRelative(BlockFace.UP))
                && (ground.isSolid() || ground.getType() == Material.WATER)
                && !UNSAFE_GROUND.contains(ground.getType());
    }

    private static boolean isClearSpace(Block block)
    {
        return block.isPassable() && !UNSAFE_SPACE.contains(block.getType());
    }

    private static Location centered(Location location)
    {
        final Location center = location.clone();
        center.setX(location.getBlockX() + 0.5);
        center.setZ(location.getBlockZ() + 0.5);
        return center;
    }

    private static Location withHeight(Location location, int y)
    {
        final Location moved = location.clone();
        moved.setY(y);
        return moved;
    }
}