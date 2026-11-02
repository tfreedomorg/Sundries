package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.StoredLocation;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.totalfreedommod.player.PlayerData;

@Command(name = "home", description = "Teleport to one of your homes.", usage = "/home [name] | /home <player> <name>")
@Permission(permission = "tfm.player.home", source = SourceType.ONLY_IN_GAME)
public class Command_home extends HomeCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(Player sender, String partial)
    {
        return matchingHomesOrPlayers(sender, partial);
    }

    @Completer(value = "", position = 1)
    public List<String> completeOwnersHome(Player sender, String partial, List<String> priorArgs)
    {
        return matchingHomesOf(sender, partial, priorArgs);
    }

    @Callback
    public void goToOnlyHome(Player sender)
    {
        final List<String> names = homes().getHomeNames(sender.getUniqueId());

        if (names.size() == 1)
            goHome(sender, names.getFirst());
        else
            listHomes(sender);
    }

    @Callback
    public void goHome(Player sender, String name)
    {
        teleportHome(sender, plugin().players().getData(sender), name, false,
                "<success>Teleported to home <highlight><home></highlight>.");
    }

    // The first argument has to be called name here too, or tab completion breaks for both forms
    @Callback
    @Permission(permission = "tfm.admin.home.others", source = SourceType.ONLY_IN_GAME)
    public void goToPlayersHome(Player sender, String name, String home)
    {
        findPlayer(sender, name).ifPresent(owner -> teleportHome(sender, owner, home, true,
                "<success>Teleported to <highlight><owner></highlight>'s home <highlight><home></highlight>."));
    }

    private void teleportHome(Player sender, PlayerData owner, String home, boolean ignoreSafety, String message)
    {
        homes().findHome(owner.getUuid(), home, found ->
        {
            if (found.isEmpty())
            {
                msg(sender, "<red>No home found with that name.");
                return;
            }

            final Optional<Location> destination = found.flatMap(StoredLocation::toLocation);

            if (destination.isEmpty())
            {
                msg(sender, "<red>The world that home is in isn't available.");
                return;
            }

            services().require(TeleportService.class)
                      .teleport(sender, destination.get(), ignoreSafety)
                      .thenAccept(teleported ->
                      {
                        if (teleported)
                            success(sender, message,
                                    Placeholder.unparsed("owner", owner.getUsername()),
                                    Placeholder.unparsed("home", home.toLowerCase()));
                        else
                            msg(sender, "<red>Could not teleport to that home.");
                      });
        });
    }
}