package lt.mredgariux.regions;

import lt.mredgariux.messages.chat.ChatManager;
import lt.mredgariux.messages.language.LanguageManager;
import lt.mredgariux.regions.api.RegionAPI;
import lt.mredgariux.regions.classes.DatabaseManager;
import lt.mredgariux.regions.classes.PluginConfig;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionManager;
import lt.mredgariux.regions.databases.repositories.RegionRepository;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.events.WorldEditEvent;
import lt.mredgariux.regions.utils.RegistrarCenter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.Set;

public final class main extends JavaPlugin {

    private final PluginConfig config = new PluginConfig(getConfig());
    private final RegionManager regionManager = new RegionManager();
    private LanguageManager lang;
    private ChatManager chat;
    private DatabaseManager databaseManager;

    private RegionAPI api;

    private BukkitTask saveTask;

    @Override
    public void onEnable() {
        // Plugin startup logic

        try {
            PluginManager pluginManager = getServer().getPluginManager();
            if (!pluginManager.isPluginEnabled("WorldEdit")) {
                getLogger().severe("[Regions | Requirements] WorldEdit plugin is not enabled!");
                getServer().getPluginManager().disablePlugin(this);
            }
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                getLogger().warning("[Regions | Optionals] Could not find PlaceholderAPI! You may not be able to use placeholders in messages.");
            }
            if (!this.getDataFolder().exists()) {
                if (!getDataFolder().mkdir()) {
                    getLogger().severe("Could not create data folder!");
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }

                this.saveDefaultConfig();
            }
            FileConfiguration config = this.getConfig();

            lang = new LanguageManager(this, LangKey.class, config.getString("language", "en"));
            lang.loadLanguages();

            chat = new ChatManager(lang, getServer(), LangKey.PREFIX);

            databaseManager = new DatabaseManager(this);
            databaseManager.connect();

            RegionRepository regionRepository = new RegionRepository(databaseManager);

            Set<Region> regions = regionRepository.getRegions();
            for (Region region : regions) {
                region.resetSync(); // Reset sync status to avoid unnecessary database updates xD
                regionManager.addRegion(region);
            }
            regions.clear();

            api = new RegionAPI(regionManager.getRegions());

            RegistrarCenter registrarCenter = new RegistrarCenter(this);

            try {
                registrarCenter.registerAll();
            } catch (Exception e) {
                getLogger().severe("[Regions | Critical] An error occurred during command / event registration:" + e.getMessage());
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            new WorldEditEvent(this);

            getLogger().info("[Regions] Plugin activated - Server security enabled.");

            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveAll, 20L, 20L);


        } catch (Exception e) {
            getLogger().severe("[Regions | Critical] An error occurred during plugin startup:" + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    public ChatManager getChatManager() {
        return chat;
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public RegionAPI getAPI() {
        return api;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("[Regions | Danger] - Plugin disabled. Server is no longer protected!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    private void saveAll() {
        if (regionManager == null || databaseManager == null) {
            return;
        }

        Set<Region> regions = regionManager.getRegionsNeedSync();
        if (regions.isEmpty()) {
            return;
        }

        RegionRepository regionRepository = new RegionRepository(databaseManager);

        try {
            regionRepository.saveRegions(regions);
        } catch (SQLException e) {
            getLogger().severe("[Regions | Critical] An error occurred while saving regions to the database: " + e.getMessage());
            saveTask.cancel();
        }
    }
}
