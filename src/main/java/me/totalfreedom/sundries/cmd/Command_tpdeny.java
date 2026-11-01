package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportRequest;
import me.totalfreedom.sundries.teleport.TeleportRequestService;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

@Command(name = "tpdeny", description = "Deny a teleport request.", usage = "/tpdeny [player]", aliases = {"tpno"})
@Permission(permission = "tfm.player.tpdeny", source = SourceType.ONLY_IN_GAME)
public class Command_tpdeny extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeRequester(Player sender, String partial)
    {
        return FuzzyMatch.filter(services().require(TeleportRequestService.class).requesterNames(sender.getUniqueId()), partial);
    }

    @Callback
    public void denyNewest(Player sender)
    {
        deny(sender, services().require(TeleportRequestService.class).takeNewest(sender.getUniqueId()));
    }

    @Callback
    public void denyFrom(Player sender, Player requester)
    {
        deny(sender, services().require(TeleportRequestService.class).takeFrom(sender.getUniqueId(), requester.getUniqueId()));
    }

    private void deny(Player sender, Optional<TeleportRequest> request)
    {
        if (request.isEmpty())
        {
            msg(sender, "<red>You have no matching teleport request.");
            return;
        }

        success(sender, "<success>Request denied.");

        Optional.ofNullable(server().getPlayer(request.get().requester()))
                .ifPresent(requester -> success(requester, "<highlight><receiver></highlight><success> denied your request.",
                           playerName("receiver", sender)));
    }
}