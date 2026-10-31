package me.totalfreedom.sundries.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.totalfreedom.api.cmd.FCommand;
import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.totalfreedommod.cmd.NameCandidates;

@Command(name = "tp", description = "Teleport to a player.", usage = "/tp <player> [target]", aliases = {"teleport", "tp2p"})
@Permission(permission = "tfm.player.tp", source = SourceType.ONLY_IN_GAME)
public class Command_tp extends FCommand
{
	@Completer(value = "", position = 0)
	public List<String> completeFirstPlayer(CommandSender sender, String partial)
	{
		return NameCandidates.online(server(), partial);
	}

	@Completer(value = "", position = 1)
	public List<String> completeSecondPlayer(CommandSender sender, String partial)
	{
		return NameCandidates.online(server(), partial);
	}

	@Callback
	public void teleportSelf(Player sender, Player target)
	{
		msg(sender, "<gray>TFSundries: not implemented yet!");
	}

	@Callback
	@Permission(permission = "tfm.admin.tp.others", source = SourceType.ONLY_IN_GAME)
	public void teleportOther(Player sender, Player target, Player destination)
	{
		msg(sender, "<gray>TFSundries: not implemented yet!");
	}
}