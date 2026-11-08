package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.sundries.warp.Warp;

@Command(name = "warp", description = "Teleport to a warp.", usage = "/warp [name]", aliases = {"warps"})
@Permission(permission = "tfm.player.warp", source = SourceType.ONLY_IN_GAME)
public class Command_warp extends WarpCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeWarp(Player sender, String partial)
    {
        return matchingWarps(partial);
    }

    @Callback
    public void list(Player sender)
    {
        listWarps(sender);
    }

    @Callback
    public void warp(Player sender, String name)
    {
        final Optional<Warp> found = warps().getWarp(name);

        if (found.isEmpty())
        {
            msg(sender, "<red>No warp found with that name.");
            return;
        }

        final Optional<Location> destination = found.get().location().toLocation();

        if (destination.isEmpty())
        {
            msg(sender, "<red>The world that warp is in isn't available.");
            return;
        }

        services().require(TeleportService.class)
                  .teleport(sender, destination.get(), false)
                  .thenAccept(teleported ->
                  {
                    if (teleported)
                        success(sender, "<success>Teleported to warp <highlight><warp></highlight>.",
                                Placeholder.unparsed("warp", found.get().name()));
                    else
                        msg(sender, "<red>Could not teleport to that warp safely.");
                  });
    }
}