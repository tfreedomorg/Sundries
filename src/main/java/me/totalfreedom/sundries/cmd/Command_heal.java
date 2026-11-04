package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Completer;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "heal", description = "Restores player health fully.", usage = "/heal [player]")
@Permission(permission = "tfm.player.heal", source = SourceType.ONLY_IN_GAME)
public class Command_heal extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(CommandSender sender, String partial)
    {
        if (!isAdmin(sender))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    public void healSelf(Player sender)
    {
       healOthers(sender, sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.heal.others")
    public void healOthers(Player sender, Player target)
    {
        final AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null)
        {
            msg(sender, "<red>Could not determine maximum health.");
            return;
        }

        target.setHealth(maxHealth.getValue());
        target.setFoodLevel(20);
        target.setSaturation(20.0F);
        target.setFireTicks(0);
        target.clearActivePotionEffects();

        if (sender.equals(target))
            success(sender, "<success>You have been fully healed.");
        else
        {
            success(sender, "<success>Fully healed <highlight><player></highlight>.",
                    playerName("player", target));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> fully healed you."
                            : "<success>An admin fully healed you.",
                    playerName("sender", sender));
        }
    }
}