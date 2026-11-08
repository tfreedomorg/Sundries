package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.warp.Warp;

@Command(name = "delwarp", description = "Delete one of your warps.", usage = "/delwarp <name>", aliases = {"remwarp", "rmwarp"})
@Permission(permission = "tfm.player.delwarp", source = SourceType.ONLY_IN_GAME)
public class Command_delwarp extends WarpCommand
{
    private static final String OTHERS_PERMISSION = "tfm.admin.delwarp.others";

    @Completer(value = "", position = 0)
    public List<String> completeWarp(Player sender, String partial)
    {
        return changeableWarps(sender, partial, OTHERS_PERMISSION);
    }

    @Callback
    public void deleteWarp(Player sender, String name)
    {
        final Optional<Warp> found = warps().getWarp(name);

        if (found.isEmpty())
        {
            msg(sender, "<red>No warp found with that name.");
            return;
        }

        if (!canChange(sender, found.get(), OTHERS_PERMISSION))
        {
            msg(sender, "<red>That warp belongs to someone else.");
            return;
        }

        warps().deleteWarp(name);
        success(sender, "<success>Deleted warp <highlight><warp></highlight>.",
                Placeholder.unparsed("warp", found.get().name()));
    }
}