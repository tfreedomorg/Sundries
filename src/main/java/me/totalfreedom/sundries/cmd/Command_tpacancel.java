package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportRequestService;

@Command(name = "tpacancel", description = "Cancel your teleport requests.", usage = "/tpacancel [player]")
@Permission(permission = "tfm.player.tpacancel", source = SourceType.ONLY_IN_GAME)
public class Command_tpacancel extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void cancelAll(Player sender)
    {
        final int cancelled = services().require(TeleportRequestService.class).cancelAll(sender.getUniqueId());

        if (cancelled == 0)
            msg(sender, "<red>You have no pending teleport requests.");
        else
            success(sender, "<success>Cancelled your teleport requests.");
    }

    @Callback
    public void cancelTo(Player sender, Player target)
    {
        final boolean cancelled = services().require(TeleportRequestService.class)
                                            .takeFrom(target.getUniqueId(), sender.getUniqueId())
                                            .isPresent();

        if (cancelled)
            success(sender, "<success>Cancelled your teleport request.");
        else
            msg(sender, "<red>You have no pending request to that player.");
    }
}