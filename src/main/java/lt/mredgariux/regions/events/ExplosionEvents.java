package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

public class ExplosionEvents extends PluginListener {
    public ExplosionEvents(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onTntPrime(TNTPrimeEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.TNT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderDragonBlockDamage(EntityExplodeEvent event) {
        if (event.getEntityType().name().equals("ENDER_DRAGON") || event.getEntityType().name().equals("ENDER_DRAGON_PART")) {
            for (Block block : event.blockList()) {
                Location loc = block.getLocation();
                Region highestPriorityRegion = regionManager.getRegionByLocation(loc);
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.ENDER_DRAGON_DESTROY_BLOCKS)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
