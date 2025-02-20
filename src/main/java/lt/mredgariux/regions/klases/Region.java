package lt.mredgariux.regions.klases;

import org.bukkit.Location;

import java.util.UUID;

public class Region {
    private final String name;
    private Location pos1;
    private Location pos2;
    private final UUID owner;
    private RegionFlags flags = new RegionFlags();

    public Region(String name, Location pos1, Location pos2, UUID owner) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void reSetPositions(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public RegionFlags getFlags() {
        return flags;
    }

    public void setFlags(RegionFlags flags) {
        this.flags = flags;
    }
}
