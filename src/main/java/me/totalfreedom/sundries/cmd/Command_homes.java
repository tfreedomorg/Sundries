package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;

@Command(name = "homes", description = "List your homes.", usage = "/homes [player]")
@Permission(permission = "tfm.player.homes", source = SourceType.ONLY_IN_GAME)
public class Command_homes extends HomeCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(Player sender, String partial)
    {
        return isAdmin(sender) ? visibleNames(sender, partial) : List.of();
    }

    @Callback
    public void list(Player sender)
    {
        listHomes(sender);
    }

    @Callback
    @Permission(permission = "tfm.admin.homes.others", source = SourceType.ONLY_IN_GAME)
    public void listPlayersHomes(Player sender, String target)
    {
        findPlayer(sender, target).ifPresent(owner -> homes().findHomes(owner.getUuid(), found ->
        {
            if (found.isEmpty())
            {
                msg(sender, "<red>That player doesn't have any homes.");
                return;
            }

            success(sender, "<highlight><owner></highlight><success>'s homes: <highlight><homes></highlight>",
                    Placeholder.unparsed("owner", owner.getUsername()),
                    nameList("homes", found.keySet()));
        }));
    }
}