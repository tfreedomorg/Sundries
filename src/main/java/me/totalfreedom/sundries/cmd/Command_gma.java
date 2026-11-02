package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.annotation.Subcommand;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "gma", description = "Quickly change your own gamemode to adventure, or define someone's username to change theirs.", usage = "/gma <-a | [player]>", aliases = {"adventure"})
@Permission(permission = "tfm.player.adventure")
public class Command_gma extends GamemodeCommand
{
    @Callback
    @Permission(permission = "tfm.player.adventure", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender, GameMode.ADVENTURE);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeAll(Player sender)
    {
        server().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.ADVENTURE));
        adminAction(sender, "<red>Changing everyone's gamemode to adventure");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject, GameMode.ADVENTURE);
    }
}