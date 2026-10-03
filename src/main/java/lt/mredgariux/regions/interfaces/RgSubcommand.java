package lt.mredgariux.regions.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class RgSubcommand extends PluginComponent {
    protected RgSubcommand(Plugin plugin) {
        super(plugin);
    }

    public abstract void execute(CommandSender sender, String[] args);
}
