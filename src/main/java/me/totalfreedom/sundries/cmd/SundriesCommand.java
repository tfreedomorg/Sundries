package me.totalfreedom.sundries.cmd;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import me.totalfreedom.api.cmd.FCommand;
import me.totalfreedom.api.framework.IServiceManager;
import me.totalfreedom.sundries.Sundries;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

public abstract class SundriesCommand extends FCommand
{
    protected final IServiceManager services()
    {
        return sundries().services();
    }

    /**
     * Sends a message that can use the configured {@code <success>} and {@code <highlight>} colors.
     */
    protected void success(CommandSender sender, String message, TagResolver... refs)
    {
        msg(sender, message, sundries().config().getMessageColors(), TagResolver.resolver(refs));
    }

    protected List<String> visibleNames(CommandSender sender, String partial)
    {
        return FuzzyMatch.filter(
                plugin().vanish().visiblePlayersFor(sender)
                        .stream()
                        .map(Player::getName)
                        .sorted()
                        .toList(),
                partial);
    }

    /**
     * A tag for {@code player}'s name in their scoreboard team's color. Without one, the name
     * takes the color of the surrounding tag, such as {@code <highlight>}.
     */
    protected TagResolver playerName(String key, Player player)
    {
        return Placeholder.component(key, Component.text(player.getName()).color(teamColor(player).orElse(null)));
    }

    /**
     * A tag listing the names, to be wrapped in {@code <highlight>}. The commas take the surrounding
     * color because we are professionals here. Only pass names limited to letters,
     * numbers, _, and -, like homes and warps, because the list is parsed.
     */
    protected TagResolver nameList(String key, Collection<String> names)
    {
        return Placeholder.parsed(key, names.stream()
                                            .sorted()
                                            .collect(Collectors.joining("</highlight>, <highlight>")));
    }

    protected final Sundries sundries()
    {
        return JavaPlugin.getPlugin(Sundries.class);
    }

    private Optional<TextColor> teamColor(Player player)
    {
        return Optional.ofNullable(server().getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()))
                       .filter(Team::hasColor)
                       .map(Team::color)
                       .filter(color -> !NamedTextColor.WHITE.equals(color));
    }
}