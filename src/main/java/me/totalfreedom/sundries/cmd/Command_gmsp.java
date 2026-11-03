package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.annotation.Subcommand;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "gmsp", description = "Quickly change your own gamemode to spectator.", usage = "/gmsp <-a | [player]>", aliases = {"spectator"})
@Permission(permission = "tfm.player.spectator")
public class Command_gmsp extends GamemodeCommand
{
    @Callback
    @Permission(permission = "tfm.player.spectator", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender, GameMode.SPECTATOR);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeAll(CommandSender sender)
    {
        server().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
        adminAction(sender, "<red>Changing everyone's gamemode to spectator");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject, GameMode.SPECTATOR);
    }
}