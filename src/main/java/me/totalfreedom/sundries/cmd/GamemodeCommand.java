package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

public abstract class GamemodeCommand extends SundriesCommand
{
    protected static final List<String> GAMEMODES = List.of(
            "creative", "c",
            "survival", "s",
            "adventure", "a",
            "spectator", "sp"
    );

    /**
     * Changes a player's gamemode and notifies the sender and target.
     * Respects vanish status when identifying the sender to the target.
     *
     * @param sender The player changing the gamemode.
     * @param target The player whose gamemode is being changed.
     * @param gamemode The gamemode to set.
     */
    protected void changeGamemode(Player sender, Player target, GameMode gamemode)
    {
        target.setGameMode(gamemode);

        final String modeName = gamemode.name().toLowerCase();

        if (sender.equals(target))
            success(sender, "<success>Your gamemode has been set to <highlight><mode></highlight>.", Placeholder.unparsed("mode", modeName));
        else
        {
            success(sender, "<success>Setting <highlight><player></highlight>'s gamemode to <highlight><mode></highlight>.", Placeholder.unparsed("mode", modeName),
                    playerName("player", target));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> set your gamemode to <highlight><mode></highlight>."
                            : "<success>An admin set your gamemode to <highlight><mode></highlight>.",
                    playerName("sender", sender), Placeholder.unparsed("mode", modeName));
        }
    }

    /**
     * Returns gamemode names and aliases matching the given partial input.
     *
     * @param partial The partial gamemode name to match.
     * @return A list of matching gamemode names and aliases.
     */
    protected List<String> matchingGamemodes(String partial)
    {
        return FuzzyMatch.filter(GAMEMODES, partial);
    }

    /**
     * Parses a gamemode name or alias into its corresponding Bukkit GameMode.
     *
     * @param mode The gamemode name or alias to parse.
     * @return An Optional containing the matching gamemode, or empty if invalid.
     */
    protected Optional<GameMode> parseGamemode(String mode)
    {
        return switch (mode.toLowerCase())
        {
            case "c", "creative" -> Optional.of(GameMode.CREATIVE);
            case "s", "survival" -> Optional.of(GameMode.SURVIVAL);
            case "a", "adventure" -> Optional.of(GameMode.ADVENTURE);
            case "sp", "spectator" -> Optional.of(GameMode.SPECTATOR);
            default -> Optional.empty();
        };
    }
}