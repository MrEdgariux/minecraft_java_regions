package lt.mredgariux.regions.databases;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection;
    private final Plugin plugin;
    private final Gson gson = new Gson();

    public Database(Plugin plugin) {
        this.plugin = plugin;
    }

    // Open the database connection
    public void connect() {
        try {
            File pathas = plugin.getDataFolder();
            connection = DriverManager.getConnection("jdbc:sqlite:" + pathas.getPath() + "/regions_v1.db");
            plugin.getLogger().info("Connected to database");
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    // Close the connection when plugin disables
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                plugin.getLogger().info("Connection closed");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    // Get connection to execute queries
    public Connection getConnection() {
        return connection;
    }

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS regions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE, " +
                "world TEXT, " +
                "x1 INT, y1 INT, z1 INT, " +
                "x2 INT, y2 INT, z2 INT, " +
                "owner TEXT, " +
                "flags TEXT" +
                ");";
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            plugin.getLogger().info("Table 'Regions' created");
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public int insertRegion(String name, Location location1, Location location2, String owner, RegionFlags flags) {

        if (location1.getWorld() != location2.getWorld()) {
            return -1;
        }
        String sql = "INSERT INTO regions (name, world, x1, y1, z1, x2, y2, z2, owner, flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, location1.getWorld().getName());
            pstmt.setInt(3, location1.getBlockX());
            pstmt.setInt(4, location1.getBlockY());
            pstmt.setInt(5, location1.getBlockZ());
            pstmt.setInt(6, location2.getBlockX());
            pstmt.setInt(7, location2.getBlockY());
            pstmt.setInt(8, location2.getBlockZ());
            pstmt.setString(9, owner);
            pstmt.setString(10, gson.toJson(flags)); // Serialize flags as JSON

            int rowNr = pstmt.executeUpdate();
            plugin.getLogger().info("Region " + name + " inserted as " + rowNr);
            return rowNr;
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            return -1;
        }
    }

    public boolean existRegion(String name) {
        String sql = "SELECT * FROM regions WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return false;
    }


    public Region getRegion(String name) {
        String sql = "SELECT * FROM regions WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Location pos1 = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1")
                );
                Location pos2 = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")
                );
                UUID owner = UUID.fromString(rs.getString("owner"));

                // Deserialize flags
                RegionFlags flags = gson.fromJson(rs.getString("flags"), RegionFlags.class);

                Region regionas = new Region(rs.getString("name"), pos1, pos2, owner);
                regionas.setFlags(flags);

                return regionas;
            } else {
                plugin.getLogger().info("Region not found.");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return null;
    }

    public Map<String, Region> getRegionList() {
        Map<String, Region> regions = new HashMap<>();
        String sql = "SELECT * FROM regions";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Location pos1 = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1")
                );
                Location pos2 = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")
                );
                UUID owner = UUID.fromString(rs.getString("owner"));

                // Deserialize flags
                RegionFlags flags = gson.fromJson(rs.getString("flags"), RegionFlags.class);

                Region regionas = new Region(rs.getString("name"), pos1, pos2, owner);
                regionas.setFlags(flags);
                regions.put(rs.getString("name"), regionas);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return regions;
    }

    public void deleteRegion(String name) {
        String sql = "DELETE FROM regions WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            plugin.getLogger().info("Region " + name + " deleted.");
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void synchronizeRegionFlags() {
        Map<String, Region> regions = getRegionList(); // Fetch all regions
        boolean updated = false; // Track if we made any updates

        for (Region region : regions.values()) {
            RegionFlags currentFlags = region.getFlags(); // Flags stored in the database
            String storedFlagsJson = gson.toJson(currentFlags); // JSON from database

            // Parse stored JSON as an object
            JsonObject storedFlags = JsonParser.parseString(storedFlagsJson).getAsJsonObject();
            JsonObject latestFlags = gson.toJsonTree(new RegionFlags()).getAsJsonObject(); // Default flags

            boolean modified = false;

            // **Step 1: Add new flags if missing**
            for (String key : latestFlags.keySet()) {
                if (!storedFlags.has(key)) {
                    storedFlags.add(key, latestFlags.get(key));
                    modified = true;
                }
            }

            // **Step 2: Remove old flags that no longer exist**
            List<String> keysToRemove = new ArrayList<>();
            for (String key : storedFlags.keySet()) {
                if (!latestFlags.has(key) && storedFlags.get(key) != null) {
                    keysToRemove.add(key);
                    modified = true;
                }
            }
            keysToRemove.forEach(storedFlags::remove);

            // **Step 3: Update database if changes were made**
            if (modified) {
                RegionFlags updatedFlags = gson.fromJson(storedFlags, RegionFlags.class);
                updateRegionFlags(region.getName(), updatedFlags);
                updated = true;
            }
        }

        if (updated) {
            plugin.getLogger().info("Region flags synchronized successfully.");
        } else {
            plugin.getLogger().info("No flag changes detected.");
        }
    }

    public void updateRegionFlags(String regionName, RegionFlags newFlags) {
        String sql = "UPDATE regions SET flags = ? WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, gson.toJson(newFlags)); // Convert to JSON
            pstmt.setString(2, regionName);
            pstmt.executeUpdate();
            plugin.getLogger().info("Updated flags for region " + regionName);
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
