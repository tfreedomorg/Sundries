package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;

import me.totalfreedom.api.cmd.annotation.Callback;
import me.totalfreedom.api.cmd.annotation.Command;
import me.totalfreedom.api.cmd.annotation.Permission;
import me.totalfreedom.api.cmd.annotation.Subcommand;
import me.totalfreedom.api.cmd.SourceType;

@Command(name = "loom", description = "Opens up a loom.", usage = "/loom [-a]")
@Permission(permission = "tfm.player.loom", source = SourceType.ONLY_IN_GAME)
public class Command_loom extends SundriesCommand
{
    @Callback
    public void openLoomSelf(Player sender)
    {
        MenuType.LOOM.create(sender).open();
    }

    @Callback
    @Subcommand("-a")
    @Permission(permission = "tfm.loom.others")
    public void openLoomAll()
    {
        server().getOnlinePlayers().forEach(player -> MenuType.LOOM.create(player).open());
    }
}