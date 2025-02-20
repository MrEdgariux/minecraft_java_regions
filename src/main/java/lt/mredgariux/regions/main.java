package lt.mredgariux.regions;

import lt.mredgariux.regions.commands.rgCommand;
import lt.mredgariux.regions.databases.Database;
import lt.mredgariux.regions.klases.Region;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class main extends JavaPlugin {
    private List<Region> regionList;
    private Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        database = new Database(this);
        database.connect();
        database.createTables();
        regionList = database.getRegionList();

        // Commands
        this.getCommand("rg").setExecutor(new rgCommand(this));
        getLogger().info("Plugin is active, securing the server.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin is inactive, does not secure the server.");
    }

    public Database getDatabase() {
        return database;
    }

    public List<Region> getRegionList() {
        return regionList;
    }

    public void addRegion(Region region) {
        regionList.add(region);
    }
}
