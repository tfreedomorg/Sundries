package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;

@Command(name = "msgtoggle", description = "Toggle whether players can send private messages to you.", usage = "/msgtoggle [player]")
@Permission(permission = "tfm.player.msgtoggle", source = SourceType.BOTH)
public class Command_msgtoggle extends MessageCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        if (!sender.hasPermission("tfm.admin.msgtoggle.others"))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    public void toggleSelf(Player sender)
    {
        toggle(sender, sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.msgtoggle.others")
    public void toggleOther(Player sender, Player target)
    {
        toggle(sender, target);
    }

    private void toggle(Player sender, Player target)
    {
        final boolean enabled = !isMessagesEnabled(target.getUniqueId());
        setMessagesEnabled(target.getUniqueId(), enabled);

        final String message = target.equals(sender)
                ? "<success>Private messages <highlight><state></highlight>."
                : "<success>Private messages <highlight><state></highlight> for <highlight><target></highlight>.";

        success(sender, message,
                Placeholder.unparsed("state", enabled ? "enabled" : "disabled"),
                playerName("target", target));

        if (!target.equals(sender))
            success(target, enabled
                    ? "<success>An admin changed your <highlight>/msgtoggle</highlight>: you can now receive private messages."
                    : "<success>An admin changed your <highlight>/msgtoggle</highlight>: you can no longer receive private messages.");
    }
}