package me.totalfreedom.sundries.cmd;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tppos", description = "Teleport to coordinates in your current world.", usage = "/tppos <x> <y> <z>")
@Permission(permission = "tfm.admin.tppos", source = SourceType.ONLY_IN_GAME)
public class Command_tppos extends SundriesCommand
{
    @Callback
    public void teleport(Player sender, double x, double y, double z)
    {
        final Location current = sender.getLocation();
        final Location destination = new Location(current.getWorld(), x, y, z, current.getYaw(), current.getPitch());

        if (!current.getWorld().getWorldBorder().isInside(destination))
        {
            msg(sender, "<red>Those coordinates are outside the world border.");
            return;
        }

        services().require(TeleportService.class)
                  .teleport(sender, destination, true)
                  .thenAccept(teleported ->
                  {
                    if (teleported)
                        success(sender, "<success>Teleported to <highlight><coordinates></highlight>.",
                                Placeholder.unparsed("coordinates", String.format("%.1f, %.1f, %.1f", x, y, z)));
                    else
                        msg(sender, "<red>Could not teleport there.");
                  });
    }
}