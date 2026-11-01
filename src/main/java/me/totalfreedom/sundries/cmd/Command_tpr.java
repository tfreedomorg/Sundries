package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.RandomTeleportService;

@Command(name = "tpr", description = "Teleport to a random location.", usage = "/tpr", aliases = {"tprandom"})
@Permission(permission = "tfm.player.tpr", source = SourceType.ONLY_IN_GAME)
public class Command_tpr extends SundriesCommand
{
    @Callback
    public void teleportRandomly(Player sender)
    {
        final RandomTeleportService randomTeleports = services().require(RandomTeleportService.class);
        final long remaining = randomTeleports.getRemainingCooldown(sender.getUniqueId());

        if (remaining > 0 && !isAdmin(sender))
        {
            msg(sender, "<red>You must wait <seconds>s before using /tpr again.",
                    Placeholder.unparsed("seconds", String.valueOf(remaining)));
            return;
        }

        randomTeleports.teleport(sender)
                       .thenAccept(teleported ->
                       {
                        if (teleported)
                            success(sender, "<success>Teleported to a random location.");
                        else
                            msg(sender, "<red>Could not find a safe location. Try again.");
                       });
    }
}