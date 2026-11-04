package me.totalfreedom.sundries.cmd;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public abstract class SpeedCommand extends SundriesCommand
{
    /**
     * Changes a player's walking or flying speed and notifies both players.
     *
     * @param sender The player changing the speed.
     * @param target The player whose speed is being changed.
     * @param type The speed type, either walk or fly.
     * @param input The requested speed, from 0 to 10.
     */
    protected void changeSpeed(Player sender, Player target, String type, int input)
    {
        if (input > 10 || input < 0)
        {
            msg(sender, "<red>Speed must be a number between 0 and 10.");
            return;
        }

        final boolean fly = type.equals("fly");
        final float speed = getRealSpeed(input, fly);
        if (fly)
            target.setFlySpeed(speed);
        else
            target.setWalkSpeed(speed);

        if (sender.equals(target))
            success(sender, "<success>Your <highlight><type></highlight> speed has been set to <highlight><speed></highlight>.",
                    Placeholder.unparsed("type", type),
                    Formatter.number("speed", input));
        else
        {
            success(sender, "<success>Setting <highlight><player></highlight>'s <highlight><type></highlight> speed to <highlight><speed></highlight>.",
                    playerName("player", target),
                    Placeholder.unparsed("type", type),
                    Formatter.number("speed", input));

            success(target, plugin().vanish().canSee(target, sender)
                            ? "<success><highlight><sender></highlight> set your <highlight><type></highlight> speed to <highlight><speed></highlight>."
                            : "<success>An admin set your <highlight><type></highlight> speed to <highlight><speed></highlight>.",
                    playerName("sender", sender),
                    Placeholder.unparsed("type", type),
                    Formatter.number("speed", input));
        }
    }

    /**
     * Changes a player's current movement speed based on whether they are flying or walking.
     *
     * @param sender The player changing the speed.
     * @param speed The requested speed, from 0 to 10.
     * @param name The name of the player whose speed is being changed.
     */
    protected void changeOtherCurrentSpeed(Player sender, int speed, String name)
    {
        final Player target = server().getPlayerExact(name);
        if (target == null)
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        changeSpeed(sender, target, currentSpeedType(target), speed);
    }

    /**
     * Changes a player's walking or flying speed using the specified speed type.
     *
     * @param sender The player changing the speed.
     * @param speed The requested speed, from 0 to 10.
     * @param type The speed type, either walk or fly.
     * @param name The name of the player whose speed is being changed.
     */
    protected void changeOtherTypeSpeed(Player sender, int speed, String type, String name)
    {
        final Player target = server().getPlayerExact(name);
        if (target == null)
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        changeSpeed(sender, target, type, speed);
    }

    /**
     * Returns the speed type corresponding to the player's current movement state.
     *
     * @param player The player to check.
     * @return Fly if the player is flying, otherwise walk.
     */
    protected String currentSpeedType(Player player)
    {
        return player.isFlying() ? "fly" : "walk";
    }

    /**
     * Converts a speed from 0 to 10 into Bukkit's movement speed scale.
     *
     * @param input The requested speed.
     * @param fly Whether the speed is for flying.
     * @return The corresponding Bukkit speed.
     */
    protected float getRealSpeed(int input, boolean fly)
    {
        final float defaultSpeed = fly ? 0.1F : 0.2F;

        if (input == 0)
            return 0.0F;

        return defaultSpeed + ((input - 1) / 9.0F) * (1.0F - defaultSpeed);
    }
}