package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.totalfreedommod.player.PlayerData;

@Command(name = "delhome", description = "Delete one of your homes.", usage = "/delhome <name> | /delhome <player> <name>", aliases = {"remhome", "rmhome"})
@Permission(permission = "tfm.player.delhome", source = SourceType.ONLY_IN_GAME)
public class Command_delhome extends HomeCommand
{
    @Completer(value = "", position = 0)
    public List<String> completeFirst(Player sender, String partial)
    {
        return matchingHomesOrPlayers(sender, partial);
    }

    @Completer(value = "", position = 1)
    public List<String> completeOwnersHome(Player sender, String partial, List<String> priorArgs)
    {
        return matchingHomesOf(sender, partial, priorArgs);
    }

    @Callback
    public void delete(Player sender, String name)
    {
        deleteHome(sender, plugin().players().getData(sender), name, "<success>Deleted home <highlight><home></highlight>.");
    }

    // The first argument has to be called name here too, or tab completion breaks for both forms
    @Callback
    @Permission(permission = "tfm.admin.delhome.others", source = SourceType.ONLY_IN_GAME)
    public void deletePlayersHome(Player sender, String name, String home)
    {
        findPlayer(sender, name).ifPresent(owner -> deleteHome(sender, owner, home,
                "<success>Deleted <highlight><owner></highlight>'s home <highlight><home></highlight>."));
    }

    private void deleteHome(Player sender, PlayerData owner, String home, String message)
    {
        homes().deleteHome(owner.getUuid(), home, deleted ->
        {
            if (deleted)
                success(sender, message,
                        Placeholder.unparsed("owner", owner.getUsername()),
                        Placeholder.unparsed("home", home.toLowerCase()));
            else
                msg(sender, "<red>No home found with that name.");
        });
    }
}