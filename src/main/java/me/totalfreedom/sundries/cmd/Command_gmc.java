package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "gmc", description = "Quickly change your own gamemode to creative.", usage = "/gmc <-a | [player]>", aliases = {"creative"})
@Permission(permission = "tfm.player.creative")
public class Command_gmc extends GamemodeCommand
{
    @Permission(permission = "tfm.player.creative", source = SourceType.ONLY_IN_GAME)
    @Callback
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender, GameMode.CREATIVE);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeAll(CommandSender sender)
    {
        server().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.CREATIVE));
        adminAction(sender, "<red>Changing everyone's gamemode to creative");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject, GameMode.CREATIVE);
    }
}