package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.totalfreedom.totalfreedommod.player.PlayerData;

import org.bukkit.entity.Player;

import me.totalfreedom.sundries.home.HomeService;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

public abstract class HomeCommand extends SundriesCommand
{
    protected final HomeService homes()
    {
        return services().require(HomeService.class);
    }

    protected List<String> matchingHomes(Player sender, String partial)
    {
        return FuzzyMatch.filter(homes().getHomeNames(sender.getUniqueId()), partial);
    }

    /**
     * Completes the first argument of a command whose admin form takes a player there instead.
     */
    protected List<String> matchingHomesOrPlayers(Player sender, String partial)
    {
        if (!isAdmin(sender))
            return matchingHomes(sender, partial);

        return Stream.concat(matchingHomes(sender, partial).stream(), visibleNames(sender, partial).stream())
                     .distinct()
                     .toList();
    }

    /**
     * Completes the home argument of an admin form. Only online players' homes are in memory.
     */
    protected List<String> matchingHomesOf(Player sender, String partial, List<String> priorArgs)
    {
        if (!isAdmin(sender))
            return List.of();

        return Optional.ofNullable(server().getPlayerExact(priorArgs.getFirst()))
                       .map(owner -> FuzzyMatch.filter(homes().getHomeNames(owner.getUniqueId()), partial))
                       .orElse(List.of());
    }

    /**
     * @return The player with that name, or empty if there isn't one and {@code sender} was told.
     */
    protected Optional<PlayerData> findPlayer(Player sender, String name)
    {
        final Optional<PlayerData> data = Optional.ofNullable(plugin().players().getData(name));

        if (data.isEmpty())
            sender.sendMessage(PLAYER_NOT_FOUND);

        return data;
    }

    protected void listHomes(Player sender)
    {
        final List<String> names = homes().getHomeNames(sender.getUniqueId());

        if (names.isEmpty())
        {
            msg(sender, "<red>You don't have any homes. Set one with /sethome.");
            return;
        }

        success(sender, "<success>Your homes: <highlight><homes></highlight>", nameList("homes", names));
    }
}