package me.totalfreedom.sundries.cmd;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "top", description = "Teleport to the highest block above you.", usage = "/top")
@Permission(permission = "tfm.player.top", source = SourceType.ONLY_IN_GAME)
public class Command_top extends SundriesCommand
{
    @Callback
    public void teleportToTop(Player sender)
    {
        final Location destination = sender.getLocation();
        destination.setY(destination.getWorld().getHighestBlockYAt(destination) + 1);

        services().require(TeleportService.class)
                  .teleport(sender, destination, false)
                  .thenAccept(teleported ->
                  {
                    if (teleported)
                        success(sender, "<success>Teleported to the top.");
                    else
                        msg(sender, "<red>Could not teleport there safely.");
                  });
    }
}