package me.totalfreedom.sundries.teleport;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.config.ConfigEntry;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.totalfreedommod.FreedomService;

public class TeleportRequestService extends FreedomService
{
    private final MainConfig config;
    private final Map<UUID, Map<UUID, TeleportRequest>> pendingByReceiver = new HashMap<>();

    public TeleportRequestService(FreedomAPI plugin, MainConfig config)
    {
        super(plugin);
        this.config = config;
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop()
    {
        pendingByReceiver.clear();
    }

    /**
     * @return False if {@code receiver} already has the maximum number of pending requests.
     */
    public boolean send(Player requester, Player receiver, boolean here)
    {
        final Map<UUID, TeleportRequest> received = activeRequests(receiver.getUniqueId());

        if (!received.containsKey(requester.getUniqueId())
                && received.size() >= config.getInteger(ConfigEntry.TELEPORT_MAX_PENDING_REQUESTS))
            return false;

        final long timeoutMillis = TimeUnit.SECONDS.toMillis(config.getInteger(ConfigEntry.TELEPORT_REQUEST_TIMEOUT_SECONDS));

        // Removed first so that a repeat request counts as the newest
        received.remove(requester.getUniqueId());
        received.put(requester.getUniqueId(), new TeleportRequest(requester.getUniqueId(), receiver.getUniqueId(),
                here, System.currentTimeMillis() + timeoutMillis));
        return true;
    }

    public Optional<TeleportRequest> takeNewest(UUID receiver)
    {
        final Optional<TeleportRequest> newest = activeRequests(receiver).values()
                                                                         .stream()
                                                                         .reduce((older, newer) -> newer);
        newest.ifPresent(request -> activeRequests(receiver).remove(request.requester()));
        return newest;
    }

    public Optional<TeleportRequest> takeFrom(UUID receiver, UUID requester)
    {
        return Optional.ofNullable(activeRequests(receiver).remove(requester));
    }

    /**
     * @return How many of {@code requester}'s requests were cancelled.
     */
    public int cancelAll(UUID requester)
    {
        return (int) pendingByReceiver.keySet()
                                      .stream()
                                      .toList()
                                      .stream()
                                      .filter(receiver -> activeRequests(receiver).remove(requester) != null)
                                      .count();
    }

    public List<String> requesterNames(UUID receiver)
    {
        return activeRequests(receiver).keySet()
                                       .stream()
                                       .map(server::getPlayer)
                                       .filter(Objects::nonNull)
                                       .map(Player::getName)
                                       .toList();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final UUID uuid = event.getPlayer().getUniqueId();
        pendingByReceiver.remove(uuid);
        pendingByReceiver.values().forEach(received -> received.remove(uuid));
    }

    private Map<UUID, TeleportRequest> activeRequests(UUID receiver)
    {
        final Map<UUID, TeleportRequest> received = pendingByReceiver.computeIfAbsent(receiver, ignored -> new LinkedHashMap<>());
        received.values().removeIf(request -> request.expiresAt() <= System.currentTimeMillis());
        return received;
    }
}