package me.totalfreedom.sundries.cmd;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

@Command(name = "gamemode", description = "Choose your own gamemode, or define someone's username to change theirs.", usage = "/gamemode <mode> [-a | name]", aliases = {"gm"})
@Permission(permission = "tfm.player.gamemode")
public class Command_gamemode extends SundriesCommand
{
    private static final List<String> GAMEMODES = List.of(
            "creative", "c",
            "survival", "s",
            "adventure", "a",
            "spectator", "sp"
    );

    @Completer(value = "", position = 0)
    public List<String> completeFirst(Player sender, String partial)
    {
        return matchingGamemodes(partial);
    }

    @Completer(value = "", position = 1)
    public List<String> completeSecond(Player sender, String partial, List<String> priorArgs)
    {
        if (priorArgs.isEmpty() || parseGamemode(priorArgs.getFirst()) == null)
            return List.of();
        final List<String> suggestions = new ArrayList<>();
        if (isAdmin(sender))
        {
            suggestions.add("-a");
            suggestions.addAll(visibleNames(sender, partial));
        }
        return FuzzyMatch.filter(suggestions, partial);
    }

    @Callback
    @Permission(permission = "tfm.player.gamemode", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender, String mode)
    {
        final GameMode gamemode = parseGamemode(mode);

        if (gamemode == null)
        {
            msg(sender, "<red>Invalid gamemode.");
            return;
        }

        changeGamemode(sender, sender, gamemode);
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, String mode, String targetName)
    {
        final GameMode gamemode = parseGamemode(mode);

        if (gamemode == null)
        {
            msg(sender, "<red>Invalid gamemode.");
            return;
        }

        if (targetName.equalsIgnoreCase("-a"))
        {
            if (!isAdmin(sender))
            {
                msg(sender, "<red>You do not have permission to change everyone's gamemode.");
                return;
            }

            server().getOnlinePlayers().forEach(player -> setGamemode(player, gamemode));
            adminAction(sender, "<red>Changing everyone's gamemode to <mode>",
                    Placeholder.unparsed("mode", gamemode.name().toLowerCase()));
            return;
        }

        final Player target = server().getPlayerExact(targetName);
        if (target == null)
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        changeGamemode(sender, target, gamemode);
    }

    private void changeGamemode(Player sender, Player target, GameMode gamemode)
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

    private List<String> matchingGamemodes(String partial)
    {
        return FuzzyMatch.filter(GAMEMODES, partial);
    }

    private void setGamemode(Player subject, GameMode gamemode)
    {
        subject.setGameMode(gamemode);
    }

    private GameMode parseGamemode(String mode)
    {
        return switch (mode.toLowerCase())
        {
            case "c", "creative" -> GameMode.CREATIVE;
            case "s", "survival" -> GameMode.SURVIVAL;
            case "a", "adventure" -> GameMode.ADVENTURE;
            case "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }
}