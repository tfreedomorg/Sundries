package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tp", description = "Teleport to a player.", usage = "/tp <player> [target]", aliases = {"teleport", "tp2p", "tpo"})
@Permission(permission = "tfm.player.tp", source = SourceType.ONLY_IN_GAME)
public class Command_tp extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirstPlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Completer(value = "", position = 1)
    public List<String> completeSecondPlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void teleportSelf(Player sender, Player target)
    {
        if (!plugin().vanish().canSee(sender, target))
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        if (!isAdmin(sender)
                && !sender.equals(target)
                && !services().require(TeleportService.class).isTeleportEnabled(target.getUniqueId()))
        {
            msg(sender, "<red>That player has teleportation disabled.");
            return;
        }

        teleport(sender, sender, target, false);
    }

    @Callback
    @Permission(permission = "tfm.admin.tp.others", source = SourceType.ONLY_IN_GAME)
    public void teleportOther(Player sender, Player target, Player destination)
    {
        teleport(sender, target, destination, true);
    }

    private void teleport(Player sender, Player subject, Player destination, boolean ignoreSafety)
    {
        final String message = subject.equals(sender)
                ? "<success>Teleported to <highlight><destination></highlight>."
                : "<success>Teleported <highlight><subject></highlight> to <highlight><destination></highlight>,";

        services().require(TeleportService.class)
                  .teleport(subject, destination.getLocation(), ignoreSafety)
                  .thenAccept(teleported ->
                  {
                      if (teleported)
                          success(sender, message,
                                  Placeholder.unparsed("subject", subject.getName()),
                                  Placeholder.unparsed("destination", destination.getName()));
                      else
                          msg(sender, "<red>Could not teleport there safely.");
                  });
    }
}