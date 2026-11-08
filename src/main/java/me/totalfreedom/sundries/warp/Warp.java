package me.totalfreedom.sundries.warp;

import java.util.UUID;

import me.totalfreedom.sundries.teleport.StoredLocation;

public record Warp(String name, UUID creator, StoredLocation location) {}