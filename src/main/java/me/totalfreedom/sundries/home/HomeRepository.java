package me.totalfreedom.sundries.home;

import java.sql.*;
import java.util.*;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import me.totalfreedom.sundries.sql.SundriesDatabase;
import me.totalfreedom.sundries.teleport.StoredLocation;

public class HomeRepository
{
    private final SundriesDatabase database;

    public HomeRepository(SundriesDatabase database)
    {
        this.database = database;
    }

    public void createTable() throws SQLException
    {
        try (Connection connection = database.borrowConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS home (
                        uuid TEXT NOT NULL,
                        name TEXT NOT NULL,
                        world TEXT NOT NULL,
                        x REAL NOT NULL,
                        y REAL NOT NULL,
                        z REAL NOT NULL,
                        yaw REAL NOT NULL,
                        pitch REAL NOT NULL,
                        PRIMARY KEY (uuid, name)
                )""");
        }
    }

    public Mono<Map<String, StoredLocation>> loadHomes(UUID uuid)
    {
        return Mono.fromCallable(() ->
        {
            final Map<String, StoredLocation> homes = new HashMap<>();

            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM home WHERE uuid = ?"))
            {
                statement.setString(1, uuid.toString());

                try (ResultSet results = statement.executeQuery())
                {
                    while (results.next())
                        homes.put(results.getString("name"), new StoredLocation(results.getString("world"),
                                results.getDouble("x"), results.getDouble("y"), results.getDouble("z"),
                                results.getFloat("yaw"), results.getFloat("pitch")));
                }
            }

            return homes;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> saveHome(UUID uuid, String name, StoredLocation location)
    {
        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR REPLACE INTO home (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setString(3, location.world());
                statement.setDouble(4, location.x());
                statement.setDouble(5, location.y());
                statement.setDouble(6, location.z());
                statement.setFloat(7, location.yaw());
                statement.setFloat(8, location.pitch());
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteHome(UUID uuid, String name)
    {
        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM home WHERE uuid = ? AND name = ?"))
            {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}