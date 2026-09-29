package lt.mredgariux.regions.classes;

import org.bukkit.Location;

public class Region {
    private final String name;
    private final Location pos1;
    private final Location pos2;

    private final long volume;
    private RegionFlags flags = new RegionFlags();

    private boolean needSync = true;

    public Region(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;

        this.volume = calculateVolume(pos1, pos2);
    }

    private long calculateVolume(Location pos1, Location pos2) {
        long x = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1L;
        long y = Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1L;
        long z = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1L;

        return x * y * z;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public long getVolume() {
        return volume;
    }

    public boolean containsLocation(Location loc) {
        if (!loc.getWorld().equals(pos1.getWorld())) {
            return false;
        }

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());

        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());

        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return loc.getBlockX() >= minX && loc.getBlockX() <= maxX
                && loc.getBlockY() >= minY && loc.getBlockY() <= maxY
                && loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ;
    }

    public RegionFlags getFlags() {
        return flags;
    }

    public void setFlags(RegionFlags flags) {
        this.flags = flags;
        needSync = true;
    }

    public boolean needSync() {
        return (needSync || flags.isDirty());
    }

    public void resetSync() {
        needSync = false;
    }
}
