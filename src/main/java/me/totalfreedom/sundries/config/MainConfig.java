package me.totalfreedom.sundries.config;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import me.totalfreedom.sundries.util.FLog;

public class MainConfig
{
    private final JavaPlugin plugin;

    private TagResolver messageColors;

    public MainConfig(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void load()
    {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        messageColors = TagResolver.resolver(
                        Placeholder.styling("success", getColor(ConfigEntry.MESSAGES_SUCCESS_COLOR)),
                        Placeholder.styling("highlight", getColor(ConfigEntry.MESSAGES_HIGHLIGHT_COLOR)));
    }

    /**
     * @return The {@code <success>} and {@code <highlight>} tags, with the colors from the config.
     */
    public TagResolver getMessageColors()
    {
        return messageColors;
    }

    public int getInteger(ConfigEntry entry)
    {
        return plugin.getConfig().getInt(entry.getPath());
    }

    public boolean getBoolean(ConfigEntry entry)
    {
        return plugin.getConfig().getBoolean(entry.getPath());
    }

    public List<String> getStringList(ConfigEntry entry)
    {
        return plugin.getConfig().getStringList(entry.getPath());
    }

    private TextColor getColor(ConfigEntry entry)
    {
        final TextColor color = TextColor.fromHexString(plugin.getConfig().getString(entry.getPath()));

        if (color != null)
            return color;

        FLog.warn("Invalid color at %s in the Sundries config; using the default.", entry.getPath());
        return TextColor.fromHexString(plugin.getConfig().getDefaults().getString(entry.getPath()));
    }
}