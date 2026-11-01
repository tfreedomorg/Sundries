package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportRequestService;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tpa", description = "Ask to teleport to another player.", usage = "/tpa <player>", aliases = {"call", "tpask"})
@Permission(permission = "tfm.player.tpa", source = SourceType.ONLY_IN_GAME)
public class Command_tpa extends TeleportRequestCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void request(Player sender, Player target)
    {
        if (!sendRequest(sender, target, false))
            return;

        final TeleportService teleports = services().require(TeleportService.class);

        if (teleports.isTeleportEnabled(target.getUniqueId()) && teleports.isAutoAccept(target.getUniqueId()))
        {
            services().require(TeleportRequestService.class).takeFrom(target.getUniqueId(), sender.getUniqueId());
            teleports.teleport(sender, target.getLocation(), false)
                     .thenAccept(teleported ->
                     {
                        if (!teleported)
                            msg(sender, "<red>Could not teleport there safely.");
                     });
            return;
        }

        success(target, "<highlight><requester></highlight><success> wants to teleport to you. Use <highlight>/tpaccept</highlight> or <highlight>/tpdeny</highlight>.",
                playerName("requester", sender));
    }
}