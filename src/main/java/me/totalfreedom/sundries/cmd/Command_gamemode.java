package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "gamemode", description = "Choose your own gamemode.", usage = "/gamemode <mode> [-a | player]", aliases = {"gm"})
@Permission(permission = "tfm.player.gamemode")
public class Command_gamemode extends GamemodeCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(Player sender, String partial)
    {
        return matchingGamemodes(partial);
    }

    @Completer(value = "", position = 1)
    public List<String> completeSecond(Player sender, String partial, List<String> priorArgs)
    {
        if (priorArgs.isEmpty() || parseGamemode(priorArgs.getFirst()).isEmpty() || !isAdmin(sender))
            return List.of();

        return Stream.concat(
                    Stream.of("-a")
                          .filter(arg -> arg.startsWith(partial)),
                    visibleNames(sender, partial).stream()
                ).toList();
    }

    @Callback
    @Permission(permission = "tfm.player.gamemode", source = SourceType.ONLY_IN_GAME)
    public void changeGamemodeSelf(Player sender, String mode)
    {
        final Optional<GameMode> gamemode = parseGamemode(mode);

        if (gamemode.isEmpty())
        {
            msg(sender, "<red>Invalid gamemode.");
            return;
        }

        changeGamemode(sender, sender, gamemode.get());
    }

    @Callback
    @Permission(permission = "tfm.admin.gamemode.others")
    public void changeGamemodeOther(Player sender, String mode, String targetName)
    {
        final Optional<GameMode> gamemode = parseGamemode(mode);

        if (gamemode.isEmpty())
        {
            msg(sender, "<red>Invalid gamemode.");
            return;
        }

        if (targetName.equalsIgnoreCase("-a"))
        {
            server().getOnlinePlayers().forEach(player -> player.setGameMode(gamemode.get()));
            adminAction(sender, "<red>Changing everyone's gamemode to <mode>",
                    Placeholder.unparsed("mode", gamemode.get().toString().toLowerCase()));
            return;
        }

        final Player target = server().getPlayerExact(targetName);
        if (target == null)
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        changeGamemode(sender, target, gamemode.get());
    }
}