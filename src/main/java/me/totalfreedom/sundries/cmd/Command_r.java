package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;

@Command(name = "r", description = "Reply to the last person who messaged you.", usage = "/r <message>", aliases = {"reply"})
@Permission(permission = "tfm.player.msg", source = SourceType.BOTH)
public class Command_r extends MessageCommand
{
    @Callback
    public void replyMessage(CommandSender sender, @Greedy String message)
    {
        replyToMessage(sender, message);
    }
}