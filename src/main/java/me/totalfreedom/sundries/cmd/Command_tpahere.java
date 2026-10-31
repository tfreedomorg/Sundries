package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;

@Command(name = "tpahere", description = "Ask a player to teleport to you.", usage = "/tpahere <player>")
@Permission(permission = "tfm.player.tpahere", source = SourceType.ONLY_IN_GAME)
public class Command_tpahere extends TeleportRequestCommand
{
    @Completer(value = "", position = 0)
    public List<String> completePlayer(CommandSender sender, String partial)
    {
        return visibleNames(sender, partial);
    }

    @Callback
    public void request(Player sender, Player target)
    {
        if (!sendRequest(sender, target, true))
            return;

        success(target, "<highlight><requester></highlight><success> wants you to teleport to them. Use <highlight>/tpaccept</highlight> or <highlight>/tpdeny</highlight>.",
                Placeholder.unparsed("requester", sender.getName()));
    }
}