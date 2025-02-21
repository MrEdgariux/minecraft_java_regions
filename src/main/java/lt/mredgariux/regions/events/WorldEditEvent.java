package lt.mredgariux.regions.events;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class WorldEditEvent {
    public WorldEditEvent() {
        try {
            WorldEdit.getInstance().getEventBus().register(new Object() {
                @Subscribe
                public void onEditSessionEvent(EditSessionEvent event) {
                    if (event.getActor() != null) {
                        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {

                            public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 pos, T block) throws WorldEditException {

                                Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(event.getWorld()).getName()), pos.x(), pos.y(), pos.z());
                                Region region = EventFunctions.getHighestPriorityRegion(loc);

                                if (region != null && !region.getFlags().useWorldEdit) {
                                    EventFunctions.sendNoSpamMessage(Bukkit.getPlayer(event.getActor().getName()), "&cYou cannot use WorldEdit in this region.");
                                    return false;
                                }

                                return getExtent().setBlock(pos, block);
                            }

                        });
                    }
                }
            });
        } catch (NoClassDefFoundError ignored) {
            Bukkit.getLogger().warning("WorldEdit not found. Skipping WorldEdit protection.");
        }
    }
}
