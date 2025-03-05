package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;

public class FireSpreadEvent implements Listener {
    @EventHandler
    public void onFireSpread(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().fireSpread) {
            if (event.getPlayer() == null) {
                event.setCancelled(true);
                return;
            }
            if (event.getPlayer().hasPermission("regions.bypass.fire." + highestPriorityRegion.getName()) && event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
                return;
            }
            event.setCancelled(true);
            EventFunctions.sendNoSpamMessage(event.getPlayer(), "&cYou cannot burn these blocks in this region.");
        }
    }

    @EventHandler
    public void onFirePlace(BlockBurnEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().fireSpread) {
            event.setCancelled(true);
        }
    }
}
