package me.totalfreedom.sundries.cmd;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "gma", description = "Quickly change your own gamemode to adventure, or define someone's username to change theirs.", usage = "/gma [-a | name]", aliases = {"adventure"})
@Permission(permission = "tfm.player.adventure")
public class Command_gma extends SundriesCommand
{
    @Callback
    @Permission(permission = "tfm.player.adventure", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.all")
    public void changeGamemodeAll(CommandSender sender)
    {
        Bukkit.getOnlinePlayers().forEach(this::setAdventure);
        adminAction(sender, "<red>Changing everyone's gamemode to adventure");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject);
    }

    private void changeGamemode(Player sender, Player subject)
    {
        setAdventure(subject);

        if (sender.equals(subject))
        {
            success(sender, "<success>Your gamemode has been set to <highlight>adventure</highlight>.");
        }
        else
        {
            success(sender, "<success>Setting <highlight><subject></highlight>'s gamemode to <highlight>adventure</highlight>.",
                    playerName("subject", subject));
            success(subject, "<success><highlight><sender></highlight> set your gamemode to <highlight>adventure</highlight>.",
                    playerName("sender", sender));
        }
    }

    private void setAdventure(Player subject)
    {
        subject.setGameMode(GameMode.ADVENTURE);
    }
}