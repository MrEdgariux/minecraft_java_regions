package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketEvents implements Listener {
    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null) {
            if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                return;
            }
            RegionFlags flags = highestPriorityRegion.getFlags();
            if (!flags.useBuckets) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot use buckets in this region.");
            }
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null) {
            if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                return;
            }
            RegionFlags flags = highestPriorityRegion.getFlags();
            if (!flags.useBuckets) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot use buckets in this region.");
            }
        }
    }
}
