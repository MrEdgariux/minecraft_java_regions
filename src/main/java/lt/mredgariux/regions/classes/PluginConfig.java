package lt.mredgariux.regions.classes;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {
    public final long cooldownMillis;

    public PluginConfig(FileConfiguration config) {
        this.cooldownMillis = config.getLong("no_spam_extension.duration", 1000);
    }
}
