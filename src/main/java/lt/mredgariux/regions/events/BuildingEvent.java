package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class BuildingEvent implements Listener {
    private final Plugin plugin;

    public BuildingEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null) {
            if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                return;
            }
            RegionFlags flags = highestPriorityRegion.getFlags();
            if (!flags.allowPlaceSpecificBlocks.isEmpty() && flags.allowPlaceSpecificBlocks.contains(event.getBlock().getType().name())) {
                return;
            }
            if (!flags.buildBlocks) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot place blocks in this region.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null) {
            if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                return;
            }
            RegionFlags flags = highestPriorityRegion.getFlags();
            if (!flags.allowBreakSpecificBlocks.isEmpty() && flags.allowBreakSpecificBlocks.contains(event.getBlock().getType().name())) {
                return;
            }
            if (!flags.breakBlocks) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot break blocks in this region.");
            }
        }
    }
}
