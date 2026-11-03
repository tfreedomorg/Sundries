package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.annotation.Subcommand;

@Command(name = "gms", description = "Quickly change your own gamemode to survival.", usage = "/gms <-a | [player]>", aliases = {"survival"})
@Permission(permission = "tfm.player.survival")
public class Command_gms extends GamemodeCommand
{
    @Callback
    @Permission(permission = "tfm.player.survival", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender, GameMode.SURVIVAL);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeAll(CommandSender sender)
    {
        server().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SURVIVAL));
        adminAction(sender, "<red>Changing everyone's gamemode to survival");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject, GameMode.SURVIVAL);
    }
}
