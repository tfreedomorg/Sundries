package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tptoggle", description = "Toggle whether players can teleport to you.", usage = "/tptoggle [player]")
@Permission(permission = "tfm.player.tptoggle", source = SourceType.ONLY_IN_GAME)
public class Command_tptoggle extends SundriesCommand
{
    @Completer(value ="", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void toggleSelf(Player sender)
    {
        toggle(sender, sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.tptoggle.others", source = SourceType.ONLY_IN_GAME)
    public void toggleOther(Player sender, Player target)
    {
        toggle(sender, target);
    }

    private void toggle(Player sender, Player target)
    {
        final TeleportService teleports = services().require(TeleportService.class);
        final boolean enabled = !teleports.isTeleportEnabled(target.getUniqueId());
        teleports.setTeleportEnabled(target.getUniqueId(), enabled);

        final String message = target.equals(sender)
                ? "<success>Teleportation <highlight><state></highlight>."
                : "<success>Teleportation <highlight><state></highlight> for <highlight><target></highlight>.";

        success(sender, message,
                Placeholder.unparsed("state", enabled ? "enabled" : "disabled"),
                playerName("target", target));

        if (!target.equals(sender))
            success(target, enabled
                    ? "<success>An admin changed your <highlight>/tptoggle</highlight>: players can now teleport to you."
                    : "<success>An admin changed your <highlight>/tptoggle</highlight>: players can no longer teleport to you.");

        if (!enabled && teleports.isAutoAccept(target.getUniqueId()))
            success(target, "<success>Auto-accept is paused until players can teleport to you again.");
    }
}