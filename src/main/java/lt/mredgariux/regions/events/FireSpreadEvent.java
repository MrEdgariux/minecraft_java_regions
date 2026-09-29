package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.plugin.Plugin;

public class FireSpreadEvent extends PluginListener {
    public FireSpreadEvent(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFireSpread(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.FIRE_SPREAD)) {
            if (event.getPlayer() == null) {
                event.setCancelled(true);
                return;
            }
            if (event.getPlayer().hasPermission("regions.bypass.fire." + highestPriorityRegion.getName()) && event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
                return;
            }
            event.setCancelled(true);
            NoSpamMessages.sendMessage(event.getPlayer(), LangKey.FLAG_FIRE_SPREAD_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    @EventHandler
    public void onFirePlace(BlockBurnEvent event) {
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);
        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.FIRE_SPREAD)) {
            event.setCancelled(true);
        }
    }
}
