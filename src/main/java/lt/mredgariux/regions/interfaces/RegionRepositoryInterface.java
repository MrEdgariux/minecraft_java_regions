package lt.mredgariux.regions.interfaces;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Set;

public interface RegionRepositoryInterface {

    // Regions

    Set<Region> getRegions();

    @Nullable
    Region getRegionById(int id);

    @Nullable
    Region getRegionByName(String name);

    void saveRegion(Region region) throws SQLException;

    void saveRegions(Set<Region> regions) throws SQLException;

    void deleteRegion(Region region) throws SQLException;

    // Region flags

    @Nullable
    RegionFlags getRegionFlagsById(int id);

    void saveRegionFlags(int id, RegionFlags regionFlags) throws SQLException;
}
