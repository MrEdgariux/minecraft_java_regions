package lt.mredgariux.regions.classes;

import lt.mredgariux.regions.enums.RegionFlagEnum;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.Locale;

public class DatabaseManager {
    private final Plugin plugin;
    private Connection connection;

    public DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        File dbFile = new File(plugin.getDataFolder(), "regions_v2.db");

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

        createTables();
        insertFlags();
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {

            // Regions table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS regions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT UNIQUE,
                        world TEXT,
                        x1 INTEGER,
                        y1 INTEGER,
                        z1 INTEGER,
                        x2 INTEGER,
                        y2 INTEGER,
                        z2 INTEGER
                    );
                    """);

            // Flags table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS flags (
                        name TEXT PRIMARY KEY NOT NULL,
                        title_key VARCHAR(255),
                        description_key VARCHAR(255)
                    );
                    """);

            // Region flags table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS region_flags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        region_id INTEGER NOT NULL,
                        flag_name TEXT NOT NULL,
                        flag_value TEXT,
                        FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE,
                        FOREIGN KEY (flag_id) REFERENCES flags(id) ON DELETE CASCADE
                    );
                    """);
        }
    }

    private void insertFlags() throws SQLException {
        String sql = """
                INSERT OR IGNORE INTO flags (name, title_key, description_key)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (RegionFlagEnum flag : RegionFlagEnum.values()) {
                String name = flag.name()
                        .toUpperCase(Locale.ROOT);

                String flagTitleKeyString = "FLAG_" + name + "_TITLE";
                String flagDescriptionKeyString = "FLAG_" + name + "_DESCRIPTION";

                statement.setString(1, name);
                statement.setString(2, flagTitleKeyString);
                statement.setString(3, flagDescriptionKeyString);

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
