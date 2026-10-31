package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportRequest;
import me.totalfreedom.sundries.teleport.TeleportRequestService;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

@Command(name = "tpaccept", description = "Accept a teleport request.", usage = "/tpaccept [player]", aliases = {"tpyes"})
@Permission(permission = "tfm.player.tpaccept", source = SourceType.ONLY_IN_GAME)
public class Command_tpaccept extends SundriesCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeRequester(Player sender, String partial)
    {
        return FuzzyMatch.filter(services().require(TeleportRequestService.class).requesterNames(sender.getUniqueId()), partial);
    }

    @Callback
    public void acceptNewest(Player sender)
    {
        accept(sender, services().require(TeleportRequestService.class).takeNewest(sender.getUniqueId()));
    }

    @Callback
    public void acceptFrom(Player sender, Player requester)
    {
        accept(sender, services().require(TeleportRequestService.class).takeFrom(sender.getUniqueId(), requester.getUniqueId()));
    }

    private void accept(Player sender, Optional<TeleportRequest> request)
    {
        final Optional<Player> requester = request.map(TeleportRequest::requester)
                                                  .map(server()::getPlayer);

        if (requester.isEmpty())
        {
            msg(sender, "<red>You have no matching teleport request.");
            return;
        }

        final Player subject = request.get().here() ? sender : requester.get();
        final Player destination = request.get().here() ? requester.get() : sender;

        services().require(TeleportService.class)
                  .teleport(subject, destination.getLocation(), false)
                  .thenAccept(teleported ->
                  {
                    if (!teleported)
                    {
                        msg(sender, "<red>Could not teleport there safely.");
                        msg(requester.get(), "<red>Could not teleport there safely.");
                        return;
                    }

                    success(sender, "<success>Request accepted.");
                    success(requester.get(), "<highlight><receiver></highlight><success> accepted your request.",
                            Placeholder.unparsed("receiver", sender.getName()));
                  });
    }
}