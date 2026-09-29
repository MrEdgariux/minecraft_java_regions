package lt.mredgariux.regions.databases.repositories;

import lt.mredgariux.regions.classes.DatabaseManager;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.RegionRepositoryInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegionRepository implements RegionRepositoryInterface {
    private final DatabaseManager dbManager;

    public RegionRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Set<Region> getRegions() {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("SELECT * FROM regions")) {
            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                Set<Region> regions = new HashSet<>();
                while (resultSet.next()) {
                    World world = Bukkit.getWorld(resultSet.getString("world"));

                    if (world == null) {
                        continue;
                    }

                    Location pos1 = new Location(
                            world,
                            resultSet.getInt("x1"),
                            resultSet.getInt("y1"),
                            resultSet.getInt("z1")
                    );

                    Location pos2 = new Location(
                            world,
                            resultSet.getInt("x2"),
                            resultSet.getInt("y2"),
                            resultSet.getInt("z2")
                    );

                    RegionFlags regionFlags = getRegionFlagsById(resultSet.getInt("id"));

                    Region reg = new Region(
                            resultSet.getString("name"),
                            pos1,
                            pos2
                    );

                    reg.setFlags(regionFlags);

                    reg.resetSync(); // No need to sync lOl (we set region flags, so we need to reset that)

                    regions.add(reg);
                }
                return regions;
            }
        } catch (java.sql.SQLException e) {

            return new HashSet<>();
        }
    }

    @Override
    public @Nullable Region getRegionById(int id) {
        return null;
    }

    @Override
    public @Nullable Region getRegionByName(String name) {
        return null;
    }

    @Override
    public void saveRegion(Region region) throws SQLException {
        String regionSQL = """
                INSERT INTO regions(name, world, x1, y1, z1, x2, y2, z2)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(name) DO UPDATE SET
                    world = excluded.world,
                    x1 = excluded.x1,
                    y1 = excluded.y1,
                    z1 = excluded.z1,
                    x2 = excluded.x2,
                    y2 = excluded.y2,
                    z2 = excluded.z2;
                """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);

            int regionId;

            // Save region
            try (PreparedStatement ps = conn.prepareStatement(regionSQL, Statement.RETURN_GENERATED_KEYS)) {
                PrepareStatementRegion(ps, region);

                ps.executeUpdate();
            }

            // Get region ID
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM regions WHERE name = ?"
            )) {
                ps.setString(1, region.getName());

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new SQLException("Region not found after saving");
                }

                regionId = rs.getInt("id");
            }

            saveRegionFlags(regionId, region.getFlags());

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRegions(Set<Region> regions) throws SQLException {
        String sql = """
                INSERT INTO regions(name, world, x1, y1, z1, x2, y2, z2)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(name) DO UPDATE SET
                    world = excluded.world,
                    x1 = excluded.x1,
                    y1 = excluded.y1,
                    z1 = excluded.z1,
                    x2 = excluded.x2,
                    y2 = excluded.y2,
                    z2 = excluded.z2;
                """;

        try (Connection conn = dbManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (Region region : regions) {

                    PrepareStatementRegion(ps, region);

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conn.commit();

        }
    }

    private void PrepareStatementRegion(PreparedStatement ps, Region region) throws SQLException {
        ps.setString(1, region.getName());
        ps.setString(2, region.getPos1().getWorld().getName());

        ps.setInt(3, region.getPos1().getBlockX());
        ps.setInt(4, region.getPos1().getBlockY());
        ps.setInt(5, region.getPos1().getBlockZ());

        ps.setInt(6, region.getPos2().getBlockX());
        ps.setInt(7, region.getPos2().getBlockY());
        ps.setInt(8, region.getPos2().getBlockZ());
    }

    @Override
    public void deleteRegion(Region region) {

    }

    @Override
    public @Nullable RegionFlags getRegionFlagsById(int id) {
        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT flag_name, flag_value FROM region_flags WHERE region_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    RegionFlags regionFlags = new RegionFlags();
                    while (rs.next()) {
                        String flagName = rs.getString("flag_name");
                        String flagValue = rs.getString("flag_value");

                        RegionFlagEnum flagEnum = RegionFlagEnum.valueOf(flagName);

                        if (flagEnum.getType() == Boolean.class) {
                            regionFlags.setBoolean(flagEnum, Boolean.parseBoolean(flagValue));
                        } else if (flagEnum.getType() == String.class) {
                            regionFlags.setString(flagEnum, flagValue);
                        } else if (flagEnum.getType() == String[].class) {
                            regionFlags.setStringArray(flagEnum, flagValue.split(","));
                        }
                    }
                    return regionFlags;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveRegionFlags(int id, RegionFlags regionFlags) throws SQLException {
        try (Connection conn = dbManager.getConnection()) {
            String sql = """
                    INSERT INTO region_flags(region_id, flag_name, flag_value)
                    VALUES (?, ?, ?)
                    ON CONFLICT(region_id, flag_name) DO UPDATE SET
                        flag_value = excluded.flag_value;
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<RegionFlagEnum, Object> entry : regionFlags.getFlags().entrySet()) {
                    ps.setInt(1, id);
                    ps.setString(2, entry.getKey().name());
                    ps.setString(3, entry.getValue().toString());

                    ps.addBatch();
                }

                ps.executeBatch();
            }
        }
    }
}
