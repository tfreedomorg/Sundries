package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Completer;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "feed", description = "Restores player hunger fully.", usage = "/feed [player]", aliases = {"eat"})
@Permission(permission = "tfm.player.feed", source = SourceType.ONLY_IN_GAME)
public class Command_feed extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(CommandSender sender, String partial)
    {
        if (!isAdmin(sender))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    public void feedSelf(Player sender)
    {
       feedOthers(sender, sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.feed.others")
    public void feedOthers(Player sender, Player target)
    {
        target.setFoodLevel(20);
        target.setSaturation(20.0F);

        if (sender.equals(target))
            success(sender, "<success>You are no longer hungry.");
        else
        {
            success(sender, "<success>Restored <highlight><player></highlight>'s hunger.",
                    playerName("player", target));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> restored your hunger."
                            : "<success>An admin restored your hunger.",
                    playerName("sender", sender));
        }
    }
}