package lt.mredgariux.regions.events;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.math.BlockVector3;

public interface RegionProtectingExtent {
    boolean onBlockChange(BlockVector3 position, com.sk89q.worldedit.world.block.BlockState block) throws WorldEditException;
}
