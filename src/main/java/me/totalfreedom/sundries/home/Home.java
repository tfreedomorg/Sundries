package me.totalfreedom.sundries.home;

import me.totalfreedom.sundries.teleport.StoredLocation;

/**
 * @param name The name as the player typed it. Lookups ignore case.
 */
public record Home(String name, StoredLocation location) {}