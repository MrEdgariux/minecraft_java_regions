package lt.mredgariux.regions;

import lt.mredgariux.regions.api.RegionAPI;
import lt.mredgariux.regions.commands.rgCommand;
import lt.mredgariux.regions.databases.Database;
import lt.mredgariux.regions.events.*;
import lt.mredgariux.regions.classes.Region;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class main extends JavaPlugin {
    private Map<String, Region> regionList;
    private Database database;
    public static RegionAPI api;

    @Override
    public void onEnable() {
        // Plugin startup logic

        PluginManager pluginManager = getServer().getPluginManager();
        if (!pluginManager.isPluginEnabled("WorldEdit")) {
            getLogger().severe("WorldEdit plugin is not enabled!");
            getServer().getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI!");
        }
        if (!this.getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                getLogger().severe("Could not create data folder!");
                getServer().getPluginManager().disablePlugin(this);
            }
        }

        database = new Database(this);
        database.connect();
        database.createTables();
        database.synchronizeRegionFlags();
        regionList = database.getRegionList();

        api = new RegionAPI(regionList);

        // Commands
        this.getCommand("rg").setExecutor(new rgCommand(this));
        Bukkit.getPluginManager().registerEvents(new BuildingEvent(this), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(new PvPEvent(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(new ExplosionEvents(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(new EntryEvents(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(new UseEvents(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents(new BucketEvents(), (Plugin) this);
        new WorldEditEvent();
        getLogger().info("Plugin is active, securing the server nxj.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin is inactive, does not secure the server.");
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
