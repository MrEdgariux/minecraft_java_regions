package lt.mredgariux.regions.api;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionAPI {
    private Map<String, Region> regions = new HashMap<>();

    public RegionAPI(Map<String, Region> regionList) {
        this.regions = regionList;
    }

    public RegionAPI() {}

    // Add a region
    public void addRegion(Region region) {
        regions.put(region.getName(), region);
    }

    // Remove a region
    public void removeRegion(String name) {
        regions.remove(name);
    }

    // Get a region by name
    public Region getRegion(String name) {
        return regions.get(name);
    }

    // Get all regions
    public Map<String, Region> getAllRegions() {
        return regions;
    }

    // Find the highest priority region at a location
    public Region getHighestPriorityRegion(Location location) {
        Region highestPriorityRegion = null;
        for (Region reg : regions.values()) {
            if (reg.containsLocation(location)) {
                if (highestPriorityRegion == null || getRegionVolume(reg) < getRegionVolume(highestPriorityRegion)) {
                    highestPriorityRegion = reg;
                }
            }
        }
        return highestPriorityRegion;
    }

    // Update region flags
    public void updateRegionFlags(String name, RegionFlags flags) {
        Region region = regions.get(name);
        if (region != null) {
            region.setFlags(flags);
        }
    }

    // Get region volume (for priority sorting)
    private int getRegionVolume(Region region) {
        Location pos1 = region.getPos1();
        Location pos2 = region.getPos2();

        int volume = Math.abs(pos1.getBlockX() - pos2.getBlockX() + 1) *
                Math.abs(pos1.getBlockY() - pos2.getBlockY() + 1) *
                Math.abs(pos1.getBlockZ() - pos2.getBlockZ() + 1);

        return volume;
    }
}
