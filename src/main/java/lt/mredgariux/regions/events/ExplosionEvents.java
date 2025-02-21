package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionEvents implements Listener {
    @EventHandler
    public void onTntPrime(TNTPrimeEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().tnt) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderDragonBlockDamage(EntityExplodeEvent event) {
        if (event.getEntityType().name().equals("ENDER_DRAGON") || event.getEntityType().name().equals("ENDER_DRAGON_PART")) {
            for (Block block : event.blockList()) {
                Location loc = block.getLocation();
                Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().enderDragonDestroyBlocks) {
                    event.setCancelled(true);
                }
            }
        } else {
            Bukkit.getLogger().info(event.getEntityType().name() + ": " + event.getEntityType());
        }
    }
}
