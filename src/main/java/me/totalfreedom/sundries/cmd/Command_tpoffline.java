package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.totalfreedommod.player.PlayerData;

@Command(name = "tpoffline", description = "Teleport to where a player last logged out.", usage = "/tpoffline <player>", aliases = {"otp", "offlinetp", "tpoff"})
@Permission(permission = "tfm.admin.tpoffline", source = SourceType.ONLY_IN_GAME)
public class Command_tpoffline extends SundriesCommand
{
    // Only online names are cheap to list, and those are the ones this command can't use
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return List.of();
    }

    @Callback
    public void teleport(Player sender, String target)
    {
        if (server().getPlayerExact(target) != null)
        {
            msg(sender, "<red>That player is online, so use /tp instead.");
            return;
        }

        final Optional<PlayerData> data = Optional.ofNullable(plugin().players().getData(target));

        if (data.isEmpty())
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        final TeleportService teleports = services().require(TeleportService.class);

        teleports.findLogoutLocation(data.get().getUuid(), logoutLocation ->
        {
            if (logoutLocation.isEmpty())
            {
                msg(sender, "<red>No logout location is known for that player.");
                return;
            }

            teleports.teleport(sender, logoutLocation.get(), true)
                     .thenAccept(teleported ->
                     {
                        if (teleported)
                            success(sender, "<success>Teleported to where <highlight><target></highlight> logged out.",
                                    Placeholder.unparsed("target", data.get().getUsername()));
                        else
                            msg(sender, "<red>Could not teleport there.");
                     });
        });
    }
}