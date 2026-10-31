package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "back", description = "Return to your last location.", usage = "/back [player]", aliases = {"return"})
@Permission(permission = "tfm.player.back", source = SourceType.ONLY_IN_GAME)
public class Command_back extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void backSelf(Player sender)
    {
        back(sender, sender, false);
    }

    @Callback
    @Permission(permission = "tfm.admin.back.others", source = SourceType.ONLY_IN_GAME)
    public void backOther(Player sender, Player target)
    {
        back(sender, target, true);
    }

    private void back(Player sender, Player target, boolean ignoreSafety)
    {
        final TeleportService teleports = services().require(TeleportService.class);
        final Optional<Location> lastLocation = teleports.getLastLocation(target.getUniqueId());

        if (lastLocation.isEmpty())
        {
            msg(sender, "<red>No previous location to return to.");
            return;
        }

        final String message = target.equals(sender)
                ? "<success>Returned to your last location."
                : "<success> Returned <highlight><target></highlight> to their last location.";

        teleports.teleport(target, lastLocation.get(), ignoreSafety)
                 .thenAccept(teleported ->
                 {
                    if (teleported)
                        success(sender, message, Placeholder.unparsed("target", target.getName()));
                    else
                        msg(sender, "<red>Could not teleport there safely.");
                 });
    }
}