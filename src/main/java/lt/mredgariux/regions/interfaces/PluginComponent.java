package lt.mredgariux.regions.interfaces;

import lt.mredgariux.messages.chat.ChatManager;
import lt.mredgariux.messages.language.LanguageManager;
import lt.mredgariux.regions.classes.PluginConfig;
import lt.mredgariux.regions.classes.RegionManager;
import lt.mredgariux.regions.main;
import org.bukkit.plugin.Plugin;

public abstract class PluginComponent {
    protected final Plugin plugin;
    protected final main main;
    protected final RegionManager regionManager;
    protected final PluginConfig config;
    protected final ChatManager chatManager;
    protected final LanguageManager languageManager;

    public PluginComponent(Plugin plugin) {
        this.plugin = plugin;
        this.main = (main) plugin;
        this.regionManager = main.getRegionManager();
        this.config = main.getPluginConfig();
        this.chatManager = main.getChatManager();
        this.languageManager = main.getLanguageManager();
    }
}