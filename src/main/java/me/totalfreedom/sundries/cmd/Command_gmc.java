package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "gmc", description = "Quickly change your own gamemode to creative, or define someone's username to change theirs.", usage = "/gmc <-a | [name]>", aliases = {"creative"})
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
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeAll(CommandSender sender)
    {
        server().getOnlinePlayers().forEach(this::setCreative);
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
            success(sender, "<success>Your gamemode has been set to <highlight>creative</highlight>.");
        else
        {
            success(sender, "<success>Setting <highlight><subject></highlight>'s gamemode to <highlight>creative</highlight>.",
                    playerName("subject", subject));
            success(subject, plugin().vanish().canSee(subject, sender)
                            ? "<success><highlight><sender></highlight> set your gamemode to <highlight>creative</highlight>."
                            : "<success>An admin set your gamemode to <highlight>creative</highlight>.",
                    playerName("sender", sender));
        }
    }

    private void setCreative(Player subject)
    {
        subject.setGameMode(GameMode.CREATIVE);
    }
}