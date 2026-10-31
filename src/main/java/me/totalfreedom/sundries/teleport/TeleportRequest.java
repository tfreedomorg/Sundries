package me.totalfreedom.sundries.teleport;

import java.util.UUID;

/**
 * @param here Where accepting moves the receiver to the requester, rather than the other way around.
 */
public record TeleportRequest(UUID requester, UUID receiver, boolean here, long expiresAt) {}