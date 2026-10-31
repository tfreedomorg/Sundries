package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.cmd.SourceType;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.sundries.teleport.TeleportService;

@Command(name = "tpauto", description = "Toggle automatically accepting /tpa requests.", usage = "/tpauto")
@Permission(permission = "tfm.player.tpauto", source = SourceType.ONLY_IN_GAME)
public class Command_tpauto extends SundriesCommand
{
    @Callback
    public void toggle(Player sender)
    {
        final TeleportService teleports = services().require(TeleportService.class);
        final boolean enabled = !teleports.isAutoAccept(sender.getUniqueId());
        teleports.setAutoAccept(sender.getUniqueId(), enabled);

        success(sender, "<success>Auto-accept <highlight><state></highlight>.",
                Placeholder.unparsed("state", enabled ? "enabled" : "disabled"));

        if (enabled && !teleports.isTeleportEnabled(sender.getUniqueId()))
            success(sender, "<success>Requests still need <highlight>/tpaccept</highlight> while <highlight>/tptoggle</highlight> is off.");
    }
}