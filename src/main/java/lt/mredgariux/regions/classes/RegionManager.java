package lt.mredgariux.regions.classes;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.*;

public class RegionManager {
    private final HashMap<String, Region> regions = new HashMap<>();

    public Map<String, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }

    public @Nullable Region getRegionByName(String name) {
        return regions.get(name);
    }

    public @Nullable Region getRegionByLocation(Location loc) {
        Region highestPriorityRegion = null;
        long smallestVolume = Long.MAX_VALUE;

        for (Region region : regions.values()) {
            if (!region.containsLocation(loc)) {
                continue;
            }

            long volume = region.getVolume();

            if (volume < smallestVolume) {
                smallestVolume = volume;
                highestPriorityRegion = region;
            }
        }

        return highestPriorityRegion;
    }

    public void addRegion(Region region) {
        regions.put(region.getName(), region);
    }

    public boolean existsRegion(String name) {
        return regions.containsKey(name);
    }

    public void removeRegion(String name) {
        regions.remove(name);
    }

    public void removeRegion(Region region) {
        regions.remove(region.getName());
    }

    public Set<Region> getRegionsNeedSync() {
        Set<Region> needSyncRegions = new HashSet<>();
        for (Region region : regions.values()) {
            if (region.needSync()) {
                needSyncRegions.add(region);
            }
        }
        return needSyncRegions;
    }
}
