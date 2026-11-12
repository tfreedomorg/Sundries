package me.totalfreedom.sundries.cmd;

import java.util.*;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class MessageCommand extends SundriesCommand
{
    private static final UUID CONSOLE_UUID = new UUID(0L, 0L);
    private static final Map<UUID, UUID> LAST_SENT_TO = new HashMap<>();
    private static final Map<UUID, UUID> LAST_RECEIVED_FROM = new HashMap<>();
    private static final Set<UUID> REPLY_TO_LAST_RECIPIENT = new HashSet<>();
    private static final Set<UUID> messagesDisabled = new HashSet<>();

    /**
     * Returns the messaging identifier for a player or the console.
     *
     * @param sender The command sender.
     * @return The sender's messaging UUID.
     */
    private UUID messageId(CommandSender sender)
    {
        return sender instanceof Player player
                ? player.getUniqueId()
                : CONSOLE_UUID;
    }

    protected boolean isReplyToLastRecipient(UUID uuid)
    {
        return REPLY_TO_LAST_RECIPIENT.contains(uuid);
    }

    protected void setReplyToLastRecipient(UUID uuid, boolean enabled)
    {
        if (enabled)
            REPLY_TO_LAST_RECIPIENT.add(uuid);
        else
            REPLY_TO_LAST_RECIPIENT.remove(uuid);
    }

    /**
     * Checks whether a player can receive private messages.
     *
     * @param uuid The UUID of the player.
     * @return Whether private messages are enabled.
     */
    protected boolean isMessagesEnabled(UUID uuid)
    {
        return !messagesDisabled.contains(uuid);
    }

    /**
     * Enables or disables incoming private messages for a player.
     *
     * @param uuid The UUID of the player.
     * @param enabled Whether private messages should be enabled.
     */
    protected void setMessagesEnabled(UUID uuid, boolean enabled)
    {
        if (enabled)
            messagesDisabled.remove(uuid);
        else
            messagesDisabled.add(uuid);
    }

    /**
     * Sends a private message and updates the message history for both participants.
     *
     * @param sender The sender sending the message.
     * @param target The recipient of the message.
     * @param message The message to send.
     */
    protected void sendPrivateMessage(CommandSender sender, CommandSender target, String message)
    {
        if (message.isBlank())
        {
            msg(sender, "<red>You cannot send an empty message.");
            return;
        }

        if (sender instanceof Player player && target instanceof Player recipient
                && !plugin().vanish().canSee(player, recipient))
        {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        if (target instanceof Player recipient && !isMessagesEnabled(recipient.getUniqueId()))
        {
            msg(sender, "<red><target> has private messages disabled.",
                    playerName("target", recipient));
            return;
        }

        success(sender, "<success>[you -> <highlight><target></highlight>] <white><message>",
                senderName("target", target),
                Placeholder.unparsed("message", message));

        success(target, "<success>[<highlight><sender></highlight> -> you] <white><message>",
                senderName("sender", sender),
                Placeholder.unparsed("message", message));

        LAST_SENT_TO.put(messageId(sender), messageId(target));
        LAST_RECEIVED_FROM.put(messageId(target), messageId(sender));
    }

    /**
     * Sends a private message to the sender's last sender or recipient,
     * depending on their reply preference.
     *
     * @param sender The command sender replying.
     * @param message The message to send.
     */
    protected void replyToMessage(CommandSender sender, String message)
    {
        final UUID uuid = messageId(sender);
        final boolean lastRecipient = isReplyToLastRecipient(uuid);

        final UUID targetUuid = lastRecipient
                ? LAST_SENT_TO.get(uuid)
                : LAST_RECEIVED_FROM.get(uuid);

        if (targetUuid == null)
        {
            msg(sender, lastRecipient
                    ? "<red>You haven't sent a private message to anyone yet."
                    : "<red>Nobody has sent you a private message yet.");
            return;
        }

        final CommandSender target = targetUuid.equals(CONSOLE_UUID)
                ? server().getConsoleSender()
                : server().getPlayer(targetUuid);

        if (target == null)
        {
            msg(sender, "<red>The player you are trying to reply to is no longer online.");
            return;
        }

        sendPrivateMessage(sender, target, message);
    }

    /**
     * Returns a name resolver for a player or the console.
     *
     * @param key The MiniMessage placeholder key.
     * @param sender The command sender.
     * @return The sender's name as a TagResolver.
     */
    private TagResolver senderName(String key, CommandSender sender)
    {
        return sender instanceof Player player
                ? playerName(key, player)
                : Placeholder.unparsed(key, sender.getName());
    }
}