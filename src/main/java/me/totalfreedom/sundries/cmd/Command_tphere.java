package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tphere", description = "Teleport a player to you.", usage = "/tphere <player>", aliases = {"s", "tpohere"})
@Permission(permission = "tfm.admin.tphere", source = SourceType.ONLY_IN_GAME)
public class Command_tphere extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void teleportHere(Player sender, Player target)
    {
        services().require(TeleportService.class)
                  .teleport(target, sender.getLocation(), true)
                  .thenAccept(teleported ->
                  {
                    if (teleported)
                        success(sender, "<success>Teleported <highlight><target></highlight> to you.",
                                playerName("target", target));
                    else
                        msg(sender, "<red>Could not teleport that player here.");
                  });
    }
}