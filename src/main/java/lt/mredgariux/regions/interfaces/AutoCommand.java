package lt.mredgariux.regions.interfaces;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

public abstract class AutoCommand extends PluginComponent implements CommandExecutor {
    public AutoCommand(Plugin plugin) {
        super(plugin);
    }

    public abstract String getName();
}
