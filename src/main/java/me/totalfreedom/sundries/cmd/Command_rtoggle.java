package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;

@Command(name = "rtoggle", description = "Change whether the recipient of the reply is the last recipient or last sender.", usage = "/rtoggle")
@Permission(permission = "tfm.player.rtoggle", source = SourceType.ONLY_IN_GAME)
public class Command_rtoggle extends MessageCommand
{
    @Callback
    public void toggleReply(Player sender)
    {
        final boolean enabled = !isReplyToLastRecipient(sender.getUniqueId());
        setReplyToLastRecipient(sender.getUniqueId(), enabled);

        success(sender, enabled
                ? "<success>Replies will now target the <highlight>last recipient</highlight>."
                : "<success>Replies will now target the <highlight>last sender</highlight>.");
    }
}