package me.totalfreedom.sundries.warp;

import java.sql.*;
import java.util.*;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import me.totalfreedom.sundries.sql.SundriesDatabase;
import me.totalfreedom.sundries.teleport.StoredLocation;

public class WarpRepository
{
    private final SundriesDatabase database;

    public WarpRepository(SundriesDatabase database)
    {
        this.database = database;
    }

    public void createTable() throws SQLException
    {
        try (Connection connection = database.borrowConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS warp (
                        name TEXT NOT NULL PRIMARY KEY,
                        creator TEXT NOT NULL,
                        world TEXT NOT NULL,
                        x REAL NOT NULL,
                        y REAL NOT NULL,
                        z REAL NOT NULL,
                        yaw REAL NOT NULL,
                        pitch REAL NOT NULL
                )""");
        }
    }

    public Mono<Map<String, Warp>> loadWarps()
    {
        return Mono.fromCallable(() ->
        {
            final Map<String, Warp> warps = new HashMap<>();

            try (Connection connection = database.borrowConnection();
                 Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery("SELECT * FROM warp"))
            {
                while (results.next())
                {
                    final String name = results.getString("name");
                    warps.put(name, new Warp(name, UUID.fromString(results.getString("creator")),
                            new StoredLocation(results.getString("world"),
                                    results.getDouble("x"), results.getDouble("y"), results.getDouble("z"),
                                    results.getFloat("yaw"), results.getFloat("pitch"))));
                }
            }

            return warps;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> saveWarp(Warp warp)
    {
        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR REPLACE INTO warp (name, creator, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setString(1, warp.name());
                statement.setString(2, warp.creator().toString());
                statement.setString(3, warp.location().world());
                statement.setDouble(4, warp.location().x());
                statement.setDouble(5, warp.location().y());
                statement.setDouble(6, warp.location().z());
                statement.setFloat(7, warp.location().yaw());
                statement.setFloat(8, warp.location().pitch());
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteWarp(String name)
    {
        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM warp WHERE name = ?"))
            {
                statement.setString(1, name);
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}