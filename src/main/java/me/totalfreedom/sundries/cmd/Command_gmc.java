package me.totalfreedom.sundries.cmd;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "gmc", description = "Quickly change your own gamemode to creative, or define someone's username to change theirs.", usage = "/gmc <-a | [partialname]>", aliases = {"creative"})
@Permission(permission = "tfm.player.creative")
public class Command_gmc extends SundriesCommand
{
    @Permission(permission = "tfm.player.creative", source = SourceType.ONLY_IN_GAME)
    @Callback
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.all")
    public void changeGamemodeAll(CommandSender sender)
    {
        Bukkit.getOnlinePlayers().forEach(this::setCreative);
        adminAction(sender, "<red>Changing everyone's gamemode to creative");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject);
    }

    private void changeGamemode(Player sender, Player subject)
    {
        setCreative(subject);

        if (sender.equals(subject))
        {
            success(sender, "<success>Your gamemode has been set to <highlight>creative</highlight>.");
        }
        else
        {
            success(sender, "<success>Setting <highlight><subject></highlight>'s gamemode to <highlight>creative</highlight>.",
                    playerName("subject", subject));
            success(subject, "<success><highlight><sender></highlight> set your gamemode to <highlight>creative</highlight>.",
                    playerName("sender", sender));
        }
    }

    private void setCreative(Player subject)
    {
        subject.setGameMode(GameMode.CREATIVE);
    }
}