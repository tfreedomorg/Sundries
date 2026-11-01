package me.totalfreedom.sundries;

import java.io.InputStream;
import java.util.Properties;

import me.totalfreedom.api.BuildInfo;
import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.api.framework.IServiceManager;
import me.totalfreedom.sundries.cmd.CommandLoader;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.sundries.teleport.RandomTeleportService;
import me.totalfreedom.sundries.sql.SundriesDatabase;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.sundries.teleport.TeleportRequestService;
import me.totalfreedom.sundries.util.FLog;
import me.totalfreedom.totalfreedommod.framework.ServiceManager;

public class SundriesAggregate
{
    private final TFSundries plugin;
    private final ServiceManager services;

    private BuildInfo buildInfo;
    private MainConfig config;

    public SundriesAggregate(TFSundries plugin, FreedomAPI api)
    {
        this.plugin = plugin;
        this.services = new ServiceManager(api);
    }

    public void enable()
    {
        buildInfo = loadBuildInfo();
        config = new MainConfig(plugin);
        config.load();

        final SundriesDatabase database = services.register(SundriesDatabase.class,
                api -> new SundriesDatabase(api, plugin.getDataFolder()));
        final TeleportService teleports = services.register(TeleportService.class, 
                api -> new TeleportService(api, config, database));
        services.register(RandomTeleportService.class, api -> new RandomTeleportService(api, config, teleports));
        services.register(TeleportRequestService.class, api -> new TeleportRequestService(api, config));
        services.register(CommandLoader.class, CommandLoader::new);

        services.start();
    }

    public void disable()
    {
        services.stop();
    }

    public IServiceManager services()
    {
        return services;
    }

    public MainConfig config()
    {
        return config;
    }

    public BuildInfo buildInfo()
    {
        return buildInfo;
    }

    private BuildInfo loadBuildInfo()
    {
        final Properties props = readResource("build.properties");
        final Properties gitprops = readResource("git.properties");

        final String author = props.getProperty("buildAuthor", "unknown");
        final String codename = props.getProperty("buildCodeName", "unknown");
        final String version = props.getProperty("buildVersion", plugin.getPluginMeta().getVersion());
        final String date = gitprops.getProperty("git.build.time", "unknown");
        final String head = gitprops.getProperty("git.commit.id.abbrev", "unknown");

        return new BuildInfo(author, codename, version, date, head);
    }

    private Properties readResource(String name)
    {
        final Properties properties = new Properties();

        try (InputStream in = plugin.getResource(name))
        {
            if (in != null)
                properties.load(in);
            else
                FLog.warn("Build resource '%s' not found in the plugin jar; using defaults.", name);
        }
        catch (Exception ex)
        {
            FLog.warn("Could not read build resource '%s'; using defaults.", name);
        }

        return properties;
    }
}