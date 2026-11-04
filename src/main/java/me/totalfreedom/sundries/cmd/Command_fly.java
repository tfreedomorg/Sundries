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

@Command(name = "fly", description = "Toggles player flight.", usage = "/fly [player]")
@Permission(permission = "tfm.player.fly")
public class Command_fly extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(CommandSender sender, String partial)
    {
        if (!isAdmin(sender))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    @Permission(permission = "tfm.player.fly", source = SourceType.ONLY_IN_GAME)
    public void changeFlySelf(Player sender)
    {
        final boolean enabled = !sender.getAllowFlight();
        sender.setAllowFlight(enabled);

        success(sender, "<success>Set fly mode <highlight><fly:enabled:disabled></highlight> for <highlight><sender></highlight>.",
                playerName("sender", sender),
                Formatter.booleanChoice("fly", enabled));
    }

    @Callback
    @Permission(permission = "tfm.admin.fly.others")
    public void changeFlyOther(Player sender, Player target)
    {
        final boolean enabled = !target.getAllowFlight();
        target.setAllowFlight(enabled);

        if (sender.equals(target))
            success(sender, "<success>Set fly mode <highlight><fly:enabled:disabled></highlight> for <highlight><sender></highlight>.",
                    playerName("sender", sender),
                    Formatter.booleanChoice("fly", enabled));
        else
        {
            success(sender, "<success>Setting <highlight><player></highlight>'s fly mode to <highlight><fly:enabled:disabled></highlight>.",
                    playerName("player", target),
                    Formatter.booleanChoice("fly", enabled));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> set your fly mode to <highlight><fly:enabled:disabled></highlight>."
                            : "<success>An admin set your fly mode to <highlight><fly:enabled:disabled></highlight>.",
                    playerName("sender", sender),
                    Formatter.booleanChoice("fly", enabled));
        }
    }
}