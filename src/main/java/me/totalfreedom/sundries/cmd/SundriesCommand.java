package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import me.totalfreedom.api.cmd.FCommand;
import me.totalfreedom.api.framework.IServiceManager;
import me.totalfreedom.sundries.TFSundries;
import me.totalfreedom.totalfreedommod.cmd.internal.FuzzyMatch;

public abstract class SundriesCommand extends FCommand
{
    protected final IServiceManager services()
    {
        return sundries().services();
    }

    /**
     * Sends a message that can use the configured {@code <success>} and {@code <highlight>} colors.
     */
    protected void success(CommandSender sender, String message, TagResolver... refs)
    {
        msg(sender, message, sundries().config().getMessageColors(), TagResolver.resolver(refs));
    }

    protected List<String> visibleNames(CommandSender sender, String partial)
    {
        return FuzzyMatch.filter(
                plugin().vanish().visiblePlayersFor(sender)
                        .stream()
                        .map(Player::getName)
                        .sorted()
                        .toList(),
                partial);
    }

    private TFSundries sundries()
    {
        return JavaPlugin.getPlugin(TFSundries.class);
    }
}