package me.totalfreedom.sundries;

import org.bukkit.plugin.java.JavaPlugin;

import me.totalfreedom.api.BuildInfo;
import me.totalfreedom.api.PluginProvider;
import me.totalfreedom.api.framework.IServiceManager;
import me.totalfreedom.sundries.config.MainConfig;
import me.totalfreedom.sundries.util.FLog;

public class Sundries extends JavaPlugin
{
    private SundriesAggregate aggregate;

    @Override
    public void onLoad()
    {
        FLog.setPluginLogger(getSLF4JLogger());
    }

    @Override
    public void onEnable()
    {
        aggregate = new SundriesAggregate(this, PluginProvider.get());
        aggregate.enable();
    }

    @Override
    public void onDisable()
    {
        if (aggregate != null)
            aggregate.disable();
    }

    public IServiceManager services()
    {
        return aggregate.services();
    }

    public MainConfig config()
    {
        return aggregate.config();
    }

    public BuildInfo buildInfo()
    {
        return aggregate.buildInfo();
    }
}