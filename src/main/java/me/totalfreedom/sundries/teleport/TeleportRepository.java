package me.totalfreedom.sundries.teleport;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
}