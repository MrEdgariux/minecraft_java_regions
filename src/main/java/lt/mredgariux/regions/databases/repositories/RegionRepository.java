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
import java.util.HashMap;
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
                    reg.setId(resultSet.getInt("id"));

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
        saveRegions(Set.of(region));
    }

    @Override
    public void saveRegions(Set<Region> regions) throws SQLException {
        if (regions.isEmpty()) {
            return;
        }

        String insertSql = """
                INSERT INTO regions(name, world, x1, y1, z1, x2, y2, z2)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(name) DO UPDATE SET
                    world = excluded.world,
                    x1 = excluded.x1,
                    y1 = excluded.y1,
                    z1 = excluded.z1,
                    x2 = excluded.x2,
                    y2 = excluded.y2,
                    z2 = excluded.z2
                RETURNING id;
                """;
        String updateSql = """
                UPDATE regions SET name = ?, world = ?, x1 = ?, y1 = ?, z1 = ?,
                    x2 = ?, y2 = ?, z2 = ?
                WHERE id = ?;
                """;

        Connection conn = dbManager.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        Map<Region, Integer> newIds = new HashMap<>();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement insert = conn.prepareStatement(insertSql);
                 PreparedStatement update = conn.prepareStatement(updateSql)) {
                for (Region region : regions) {
                    if (region.getId() == 0) {
                        PrepareStatementRegion(insert, region);
                        try (ResultSet rs = insert.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Region ID not returned after saving");
                            }
                            newIds.put(region, rs.getInt("id"));
                        }
                    } else {
                        PrepareStatementRegion(update, region);
                        update.setInt(9, region.getId());
                        update.addBatch();
                    }
                }

                for (int count : update.executeBatch()) {
                    if (count == 0) {
                        throw new SQLException("Region not found while updating by ID");
                    }
                }
            }

            try (PreparedStatement deleteFlags = conn.prepareStatement(
                         "DELETE FROM region_flags WHERE region_id = ?");
                 PreparedStatement insertFlags = conn.prepareStatement(
                         "INSERT INTO region_flags(region_id, flag_name, flag_value) VALUES (?, ?, ?)")) {
                for (Region region : regions) {
                    int regionId = newIds.getOrDefault(region, region.getId());
                    deleteFlags.setInt(1, regionId);
                    deleteFlags.addBatch();
                    PrepareStatementRegionFlags(insertFlags, regionId, region.getFlags());
                }

                deleteFlags.executeBatch();
                insertFlags.executeBatch();
            }

            conn.commit();
            newIds.forEach(Region::setId);

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
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

    private void PrepareStatementRegionFlags(PreparedStatement ps, int regionId, RegionFlags regionFlags) throws SQLException {
        for (Map.Entry<RegionFlagEnum, Object> entry : regionFlags.getFlags().entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            ps.setInt(1, regionId);
            ps.setString(2, entry.getKey().name());
            ps.setString(3, value instanceof String[] values ? String.join(",", values) : value.toString());

            ps.addBatch();
        }
    }

    @Override
    public void deleteRegion(Region region) {
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM regions WHERE id = ?")) {
            ps.setInt(1, region.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRegions(Set<Region> regions) {
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM regions WHERE id = ?")) {
            for (Region region : regions) {
                ps.setInt(1, region.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable RegionFlags getRegionFlagsById(int id) {
        Connection conn = dbManager.getConnection();
        try {
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
        Connection conn = dbManager.getConnection();
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
