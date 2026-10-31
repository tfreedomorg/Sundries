package me.totalfreedom.sundries;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.cmd.CommandLoader;
import me.totalfreedom.totalfreedommod.framework.ServiceManager;

public class SundriesAggregate
{
	private final ServiceManager services;

	public SundriesAggregate(FreedomAPI plugin)
	{
		this.services = new ServiceManager(plugin);
	}

	public void enable()
	{
		services.register(CommandLoader.class, CommandLoader::new);

		services.start();
	}

	public void disable()
	{
		services.stop();
	}
}