package me.totalfreedom.sundries.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.util.FLog;
import me.totalfreedom.totalfreedommod.FreedomService;

public class SundriesDatabase extends FreedomService
{
    private static final String FILENAME = "sundries.db";

    private final File dataFolder;

    private HikariDataSource dataSource;

    public SundriesDatabase(FreedomAPI plugin, File dataFolder)
    {
        super(plugin);
        this.dataFolder = dataFolder;
    }

    @Override
    public void onStart()
    {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:sqlite:%s", new File(dataFolder, FILENAME).getPath()));
        config.setDriverClassName("org.sqlite.JDBC");
        config.setPoolName("Sundries-SQLite");
        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(0);

        try
        {
            dataSource = new HikariDataSource(config);
            FLog.info("Connected to %s.", FILENAME);
        }
        catch (Exception ex)
        {
            FLog.error("Could not open %s.", FILENAME);
            FLog.error(ex);
        }
    }

    @Override
    public void onStop()
    {
        if (dataSource != null)
            dataSource.close();
    }

    public Connection borrowConnection() throws SQLException
    {
        if (dataSource == null)
            throw new SQLException("The Sundries database is not connected.");

        return dataSource.getConnection();
    }
}