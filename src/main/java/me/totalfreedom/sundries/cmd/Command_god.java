package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Completer;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "god", description = "Toggles god mode.", usage = "/god [player]", aliases = {"godmode", "tgm"})
@Permission(permission = "tfm.player.god", source = SourceType.ONLY_IN_GAME)
public class Command_god extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(CommandSender sender, String partial)
    {
        if (!isAdmin(sender))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    public void changeGodSelf(Player sender)
    {
       changeGodOther(sender, sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.god.others")
    public void changeGodOther(Player sender, Player target)
    {
        final boolean enabled = !target.isInvulnerable();
        target.setInvulnerable(enabled);

        if (sender.equals(target))
            success(sender, "<success>God mode <highlight><god:enabled:disabled></highlight>.",
                    Formatter.booleanChoice("god", enabled));
        else
        {
            success(sender, "<success>God mode <highlight><god:enabled:disabled></highlight> for <highlight><player></highlight>.",
                    playerName("player", target),
                    Formatter.booleanChoice("god", enabled));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> set your god mode to <highlight><god:enabled:disabled></highlight>."
                            : "<success>An admin set your god mode to <highlight><god:enabled:disabled></highlight>.",
                    playerName("sender", sender),
                    Formatter.booleanChoice("god", enabled));
        }
    }
}