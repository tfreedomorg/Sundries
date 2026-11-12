package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;

import me.totalfreedom.sundries.teleport.TeleportRequestService;

public abstract class TeleportRequestCommand extends SundriesCommand
{
    /**
     * @return True if the request was sent, or false if it was refused and {@code sender} was told why.
     */
    protected boolean sendRequest(Player sender, Player target, boolean here)
    {
        if (sender.equals(target))
        {
            msg(sender, "<red>You cannot send a teleport request to yourself.");
            return false;
        }

        if (!plugin().vanish().canSee(sender, target))
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return false;
        }

        if (!services().require(TeleportRequestService.class).send(sender, target, here))
        {
            msg(sender, "<red>That player has too many pending requests.");
            return false;
        }

        success(sender, "<success>Request sent to <highlight><target></highlight>.",
                playerName("target", target));
        return true;
    }
}