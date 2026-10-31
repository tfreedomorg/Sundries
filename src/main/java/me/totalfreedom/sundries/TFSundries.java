package me.totalfreedom.sundries;

import org.bukkit.plugin.java.JavaPlugin;

import me.totalfreedom.api.PluginProvider;
import me.totalfreedom.sundries.cmd.Command_tp;

public class TFSundries extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		final int loaded = PluginProvider.get().commands().load(Command_tp.class);
		getSLF4JLogger().info("Loaded {} commands.", loaded);
	}
}