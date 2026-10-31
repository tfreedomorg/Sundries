package me.totalfreedom.sundries.cmd;

import me.totalfreedom.api.FreedomAPI;
import me.totalfreedom.sundries.util.FLog;
import me.totalfreedom.totalfreedommod.FreedomService;

public class CommandLoader extends FreedomService
{
    public CommandLoader(FreedomAPI plugin)
    {
        super(plugin);
    }

    @Override
    public void onStart()
    {
        final int loaded = plugin.commands().load(Command_tp.class);
        FLog.info("Loaded %d commands.", loaded);
    }

    @Override
    public void onStop() {}
}