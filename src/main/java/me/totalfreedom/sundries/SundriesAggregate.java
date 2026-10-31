package me.totalfreedom.sundries;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.api.framework.IServiceManager;
import me.totalfreedom.sundries.cmd.CommandLoader;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.sundries.teleport.TeleportService;
import me.totalfreedom.totalfreedommod.framework.ServiceManager;

public class SundriesAggregate
{
    private final TFSundries plugin;
    private final ServiceManager services;

    private MainConfig config;

    public SundriesAggregate(TFSundries plugin, FreedomAPI api)
    {
        this.plugin = plugin;
        this.services = new ServiceManager(api);
    }

    public void enable()
    {
        config = new MainConfig(plugin);
        config.load();

        services.register(TeleportService.class, api -> new TeleportService(api, config));
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
}