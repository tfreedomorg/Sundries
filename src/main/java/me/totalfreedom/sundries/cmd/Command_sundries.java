package me.totalfreedom.sundries.cmd;

import org.bukkit.command.CommandSender;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import me.totalfreedom.api.BuildInfo;
import me.totalfreedom.api.cmd.annotation.*;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;

@Command(name = "sundries", description = "Shows information about Sundries or reloads it", usage = "/sundries [reload]", aliases = {"tfs"})
@Permission(permission = "tfm.server.info")
public class Command_sundries extends SundriesCommand
{
    @Subcommand("reload")
    @Permission(permission = "tfm.server.reload")
    @Callback
    public void reloadPlugin(CommandSender sender)
    {
        if (!plugin().ranks().isAdmin(sender))
        {
            showPluginInformation(sender);
            return;
        }

        sundries().config().load();
        sundries().services().stop();
        sundries().services().start();

        success(
            sender,
            "<success><name> v<version> reloaded.",
            Placeholder.unparsed("name", sundries().getPluginMeta().getName()),
            Placeholder.unparsed("version", sundries().getPluginMeta().getVersion()));
    }

    @Callback
    public void showPluginInformation(CommandSender sender)
    {
        final BuildInfo build = sundries().buildInfo();
        success(
            sender,
            """
            <success>Sundries for 'Total Freedom', the original all-op server.
            Running on <highlight><server></highlight>.
            Created by <highlight><authors></highlight>.
            Version <highlight><version></highlight>
            Compiled <highlight><date></highlight> by <highlight><author></highlight>
            Visit <highlight>https://github.com/tfreedomorg/Sundries</highlight> for more information.\
            """,
            Placeholder.unparsed("server", ConfigEntry.SERVER_NAME.getString()),
            Placeholder.unparsed("authors", String.join(", ", sundries().getPluginMeta().getAuthors())),
            Placeholder.unparsed("version", build.formattedVersion()),
            Placeholder.unparsed("date", build.date()),
            Placeholder.unparsed("author", build.author())
        );
    }
}