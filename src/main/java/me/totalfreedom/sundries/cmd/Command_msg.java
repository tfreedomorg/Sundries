package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;

import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

@Command(name = "msg", description = "Send a private message to someone.", usage = "/msg <player> <message>", aliases = {"tell", "t", "w", "m", "pm", "whisper"})
@Permission(permission = "tfm.player.msg", source = SourceType.BOTH)
public class Command_msg extends MessageCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return Stream.concat(
                visibleNames(sender, partial).stream(),
                FuzzyMatch.filter(List.of("Console"), partial).stream()
        ).toList();
    }

    @Callback
    public void sendMessage(CommandSender sender, String name, @Greedy String message)
    {
        final CommandSender target = name.equalsIgnoreCase("console")
                ? server().getConsoleSender()
                : server().getPlayerExact(name);

        sendPrivateMessage(sender, target, message);
    }
}