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

@Command(name = "gmsp",
        description = "Quickly change your own gamemode to spectator, or define someone's username to change theirs.",
        usage = "/gmsp <-a | [player]>",
        aliases = {"spectator"}
)
@Permission(permission = "tfm.player.survival")
public class Command_gmsp extends SundriesCommand
{
    @Callback
    @Permission(permission = "tfm.player.spectator", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender)
    {
        changeGamemode(sender, sender);
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.admin.gamemode.all")
    public void changeGamemodeAll(CommandSender sender)
    {
        Bukkit.getOnlinePlayers().forEach(this::setSpectator);
        adminAction(sender, "<red>Changing everyone's gamemode to spectator");
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, Player subject)
    {
        changeGamemode(sender, subject);
    }

    private void changeGamemode(Player sender, Player subject)
    {
        setSpectator(subject);

        if (sender.equals(subject))
        {
            success(sender, "<success>Your gamemode has been set to <highlight>spectator</highlight>.");
        }
        else
        {
            success(sender, "<success>Setting <highlight><subject></highlight>'s gamemode to <highlight>spectator</highlight>.",
                    playerName("subject", subject));
            success(subject, "<success><highlight><sender></highlight> set your gamemode to <highlight>spectator</highlight>.",
                    playerName("sender", sender));
        }
    }

    private void setSpectator(Player subject)
    {
        subject.setGameMode(GameMode.SPECTATOR);
    }
}