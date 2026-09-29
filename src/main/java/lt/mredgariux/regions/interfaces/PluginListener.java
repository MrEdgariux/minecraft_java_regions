package lt.mredgariux.regions.interfaces;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PluginListener extends PluginComponent implements Listener {
    public PluginListener(Plugin plugin) {
        super(plugin);
    }
}
