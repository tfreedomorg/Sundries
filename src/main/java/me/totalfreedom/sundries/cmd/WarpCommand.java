package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.totalfreedom.sundries.warp.Warp;
import me.totalfreedom.sundries.warp.WarpService;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

public abstract class WarpCommand extends SundriesCommand
{
    protected static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_-]{1,32}");

    protected final WarpService warps()
    {
        return services().require(WarpService.class);
    }

    protected List<String> matchingWarps(String partial)
    {
        return FuzzyMatch.filter(warps().getWarpNames(), partial);
    }

    /**
     * Completes only the warps {@code sender} is allowed to move or delete.
     */
    protected List<String> changeableWarps(Player sender, String partial, String othersPermission)
    {
        return FuzzyMatch.filter(warps().getWarps()
                                        .stream()
                                        .filter(warp -> canChange(sender, warp, othersPermission))
                                        .map(Warp::name)
                                        .sorted()
                                        .toList(),
                partial);
    }

    /**
     * @return Whether {@code sender} created {@code warp} or has {@code othersPermission}.
     */
    protected boolean canChange(Player sender, Warp warp, String othersPermission)
    {
        return warp.creator().equals(sender.getUniqueId()) || plugin().ranks().hasPermission(sender, othersPermission);
    }

    protected void listWarps(Player sender)
    {
        final List<String> names = warps().getWarpNames();

        if (names.isEmpty())
        {
            msg(sender, "<red>There aren't any warps. Set one with /setwarp.");
            return;
        }

        success(sender, "<success>Warps: <highlight><warps></highlight>", nameList("warps", names));
    }
}