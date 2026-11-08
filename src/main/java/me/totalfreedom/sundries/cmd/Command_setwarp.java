package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.config.ConfigEntry;
import me.totalfreedom.sundries.warp.Warp;

@Command(name = "setwarp", description = "Set a warp at your location.", usage = "/setwarp <name>", aliases = {"createwarp"})
@Permission(permission = "tfm.player.setwarp", source = SourceType.ONLY_IN_GAME)
public class Command_setwarp extends WarpCommand
{
    // Also lifts the per-player limit
    private static final String OTHERS_PERMISSION = "tfm.admin.setwarp.others";

    @Callback
    public void setWarp(Player sender, String name)
    {
        if (!VALID_NAME.matcher(name).matches())
        {
            msg(sender, "<red>Warp names can only use letters, numbers, _, and -, up to 32 characters.");
            return;
        }

        final Optional<Warp> existing = warps().getWarp(name);

        if (existing.isPresent() && !canChange(sender, existing.get(), OTHERS_PERMISSION))
        {
            msg(sender, "<red>That warp belongs to someone else.");
            return;
        }

        final int max = sundries().config().getInteger(ConfigEntry.WARPS_MAX_PER_PLAYER);
        final List<String> owned = warps().getWarpNamesBy(sender.getUniqueId());

        if (existing.isEmpty() && !plugin().ranks().hasPermission(sender, OTHERS_PERMISSION) && owned.size() >= max)
        {
            msg(sender, "<red>You've reached the limit of <max> warps. Delete one with /delwarp or reuse one of their names: <warps>",
                Placeholder.unparsed("max", String.valueOf(max)),
                Placeholder.unparsed("warps", String.join(", ", owned)));
            return;
        }

        warps().setWarp(name, sender.getUniqueId(), sender.getLocation());
        success(sender, "<success>Set warp <highlight><warp></highlight>.",
                Placeholder.unparsed("warp", name));
    }
}