package me.totalfreedom.sundries.cmd;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.config.ConfigEntry;

@Command(name = "sethome", description = "Set a home at your location.", usage = "/sethome [name]", aliases = {"createhome"})
@Permission(permission = "tfm.player.sethome", source = SourceType.ONLY_IN_GAME)
public class Command_sethome extends HomeCommand
{
    private static final String DEFAULT_NAME = "home";
    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_-]{1,32}");

    @Completer(value = "", position = 0)
    public List<String> completeHome(Player sender, String partial)
    {
        return matchingHomes(sender, partial);
    }

    @Callback
    public void setDefaultHome(Player sender)
    {
        setHome(sender, DEFAULT_NAME);
    }

    @Callback
    public void setHome(Player sender, String name)
    {
        if (!VALID_NAME.matcher(name).matches())
        {
            msg(sender, "<red>Home names can only use letters, numbers, _, and -, up to 32 characters.");
            return;
        }

        if (!homes().setHome(sender.getUniqueId(), name, sender.getLocation()))
        {
            msg(sender, "<red>You've reached the limit of <max> homes. Delete one with /delhome or reuse one of their names.",
                Placeholder.unparsed("max", String.valueOf(sundries().config().getInteger(ConfigEntry.HOMES_MAX_PER_PLAYER))));
            return;
        }

        success(sender, "<success>Set home <highlight><home></highlight>.",
                Placeholder.unparsed("home", name.toLowerCase()));
    }
}