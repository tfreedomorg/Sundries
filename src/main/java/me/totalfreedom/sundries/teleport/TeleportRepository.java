package me.totalfreedom.sundries.teleport;

import java.sql.*;
import java.util.*;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import me.totalfreedom.sundries.sql.SundriesDatabase;

public class TeleportRepository
{
    private final SundriesDatabase database;

    public TeleportRepository(SundriesDatabase database)
    {
        this.database = database;
    }

    public void createTable() throws SQLException
    {
        try (Connection connection = database.borrowConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS teleport_disabled (uuid TEXT PRIMARY KEY)");
            statement.execute("CREATE TABLE IF NOT EXISTS last_location (uuid TEXT PRIMARY KEY, world TEXT NOT NULL, "
                    + "x REAL NOT NULL, y REAL NOT NULL, z REAL NOT NULL, yaw REAL NOT NULL, pitch REAL NOT NULL)");
        }
    }

    public Set<UUID> loadDisabled() throws SQLException
    {
        final Set<UUID> disabled = new HashSet<>();

        try (Connection connection = database.borrowConnection();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery("SELECT uuid FROM teleport_disabled"))
        {
            while (results.next())
                disabled.add(UUID.fromString(results.getString("uuid")));
        }

        return disabled;
    }

    public Mono<Void> setDisabled(UUID uuid, boolean disabled)
    {
        final String sql = disabled
                ? "INSERT OR IGNORE INTO teleport_disabled (uuid) VALUES (?)"
                : "DELETE FROM teleport_disabled WHERE uuid = ?";

        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Optional<StoredLocation>> loadLastLocation(UUID uuid)
    {
        return Mono.fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM last_location WHERE uuid = ?"))
            {
                statement.setString(1, uuid.toString());

                try (ResultSet results = statement.executeQuery())
                {
                    if (!results.next())
                        return Optional.<StoredLocation>empty();

                    return Optional.of(new StoredLocation(results.getString("world"),
                            results.getDouble("x"), results.getDouble("y"), results.getDouble("z"),
                            results.getFloat("yaw"), results.getFloat("pitch")));
                }
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> saveLastLocation(UUID uuid, StoredLocation location)
    {
        return Mono.<Void>fromCallable(() ->
        {
            try (Connection connection = database.borrowConnection();
                 PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR REPLACE INTO last_location (uuid, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setString(1, uuid.toString());
                statement.setString(2, location.world());
                statement.setDouble(3, location.x());
                statement.setDouble(4, location.y());
                statement.setDouble(5, location.z());
                statement.setFloat(6, location.yaw());
                statement.setFloat(7, location.pitch());
                statement.executeUpdate();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}