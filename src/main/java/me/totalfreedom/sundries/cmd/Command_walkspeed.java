package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Completer;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "walkspeed", description = "Change your walk speed.", usage = "/walkspeed <speed> [player]", aliases = {"wspeed"})
@Permission(permission = "tfm.player.speed", source = SourceType.ONLY_IN_GAME)
public class Command_walkspeed extends SpeedCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(Player sender, String partial)
    {
        return IntStream.rangeClosed(0, 10)
                .mapToObj(String::valueOf)
                .toList();
    }

    @Completer(value = "", position = 1)
    public List<String> completeSecond(CommandSender sender, String partial)
    {
        if (!isAdmin(sender))
            return List.of();

        return visibleNames(sender, partial);
    }

    @Callback
    public void changeSpeedSelf(Player sender, int speed)
    {
        changeSpeed(sender, sender, "walk", speed);
    }

    @Callback
    @Permission(permission = "tfm.admin.speed.others")
    public void changeSpeedOther(Player sender, int speed, Player target)
    {
        changeSpeed(sender, target, "walk", speed);
    }
}
