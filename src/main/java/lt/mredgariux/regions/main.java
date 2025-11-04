package lt.mredgariux.regions;

import lt.mredgariux.regions.api.RegionAPI;
import lt.mredgariux.regions.commands.rgCommand;
import lt.mredgariux.regions.databases.Database;
import lt.mredgariux.regions.events.*;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class main extends JavaPlugin {

    private LanguageManager lang;

    private Map<String, Region> regionList;
    private Database database;
    public static RegionAPI api;

    @Override
    public void onEnable() {
        // Plugin startup logic

        String versionString = Bukkit.getBukkitVersion();
        String numericPart = versionString.split("-")[0];

        String[] parts = numericPart.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        getLogger().info("Detected Minecraft version: " + major + "." + minor + "." + patch);

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
            }
        }

        lang = new LanguageManager(this);
        lang.loadLanguages();

        database = new Database(this);
        database.connect();
        database.createTables();
        database.synchronizeRegionFlags();
        regionList = database.getRegionList();

        api = new RegionAPI(regionList);

        // Commands
        this.getCommand("rg").setExecutor(new rgCommand(this));

        // Events
        Bukkit.getPluginManager().registerEvents(new BuildingEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new PvPEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ExplosionEvents(), this);
        Bukkit.getPluginManager().registerEvents(new EntryEvents(), this);
        Bukkit.getPluginManager().registerEvents(new UseEvents(), this);
        Bukkit.getPluginManager().registerEvents(new BucketEvents(), this);
        Bukkit.getPluginManager().registerEvents(new FireSpreadEvent(), this);
        new WorldEditEvent();

        getLogger().info("[Regions] Plugin activated - Server security enabled.");

    }

    public LanguageManager getLang() {
        return lang;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[Regions | Danger] - Plugin disabled. Server is no longer protected!");
    }

    public Database getDatabase() {
        return database;
    }

    public Map<String, Region> getRegionList() {
        return regionList;
    }

    public void addRegion(Region region) {
        regionList.put(region.getName(), region);
    }

    public void updateRegion(Region region) {
        regionList.put(region.getName(), region);
    }

    public void removeRegion(Region region) {
        regionList.remove(region.getName());
    }
}
