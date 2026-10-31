package me.totalfreedom.sundries;

import org.bukkit.plugin.java.JavaPlugin;

import me.totalfreedom.api.PluginProvider;

public class TFSundries extends JavaPlugin
{
	private SundriesAggregate aggregate;

	@Override
	public void onEnable()
	{
		aggregate = new SundriesAggregate(PluginProvider.get());
		aggregate.enable();
	}

	@Override
	public void onDisable()
	{
		if (aggregate != null)
			aggregate.disable();
	}
}