package me.totalfreedom.sundries.teleport;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record StoredLocation(String world, double x, double y, double z, float yaw, float pitch)
{
    public static StoredLocation of(Location location)
    {
        return new StoredLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    /**
     * @return This location, or empty if its world isn't loaded.
     */
    public Optional<Location> toLocation()
    {
        final World loaded = Bukkit.getWorld(world);
        return Optional.ofNullable(loaded)
                       .map(value -> new Location(value, x, y, z, yaw, pitch));
    }
}