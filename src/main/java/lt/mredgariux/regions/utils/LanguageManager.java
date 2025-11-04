package lt.mredgariux.regions.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private String defaultLang = "en";

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "langs");
        if (!langFolder.exists()) langFolder.mkdirs();

        // Copy default en.yml if missing
        File defaultFile = new File(langFolder, "en.yml");
        if (!defaultFile.exists()) {
            plugin.saveResource("langs/en.yml", false);
        }

        // Load all .yml files
        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".yml", "").toLowerCase();
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            languages.put(langCode, config);
            plugin.getLogger().info("Loaded language: " + langCode);
        }
    }

    /**
     * Get message by key with optional args, placeholders, and player context.
     */
    public String get(Player player, String key, Object... args) {
        String lang = defaultLang;
        return format(getRaw(lang, key), player, args);
    }

    public String get(String key, Object... args) {
        return format(getRaw(defaultLang, key), null, args);
    }

    /**
     * Get raw message without formatting.
     */
    private String getRaw(String lang, String key) {
        FileConfiguration cfg = languages.getOrDefault(lang, languages.get(defaultLang));
        String value = cfg.getString(key);
        if (value == null) {
            plugin.getLogger().warning("Could not find translation for key: " + key + " in language: " + lang);
            return key;
        }
        return value;
    }

    /**
     * Format message: replace %prefix%, %0%, %1%, etc.
     * Apply PlaceholderAPI if available.
     */
    private String format(String message, Player player, Object... args) {
        if (message == null) return "";

        // Replace numeric args (%0%, %1%, etc.)
        for (int i = 0; i < args.length; i++) {
            message = message.replace("%" + i + "%", String.valueOf(args[i]));
        }

        // Apply PlaceholderAPI if present and player != null
        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        // Translate color codes (&)
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public void setDefaultLang(String lang) {
        if (languages.containsKey(lang)) {
            defaultLang = lang;
        } else {
            plugin.getLogger().warning("Language not found: " + lang);
        }
    }

    public String getDefaultLang() {
        return defaultLang;
    }
}
