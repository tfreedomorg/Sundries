package me.totalfreedom.sundries.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FLog
{
    private static Logger pluginLogger = LoggerFactory.getLogger("TFSundries");

    private FLog() {}

    public static void setPluginLogger(Logger logger)
    {
        pluginLogger = logger;
    }

    public static void info(String format, Object... args)
    {
        pluginLogger.info(String.format(format, args));
    }

    public static void warn(String format, Object... args)
    {
        pluginLogger.warn(String.format(format, args));
    }

    public static void error(String format, Object... args)
    {
        pluginLogger.error(String.format(format, args));
    }

    public static void error(Throwable ex)
    {
        pluginLogger.error("", ex);
    }
}