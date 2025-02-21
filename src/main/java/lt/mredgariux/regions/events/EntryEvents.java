package lt.mredgariux.regions.events;

import lt.mredgariux.regions.api.RegionEnterEvent;
import lt.mredgariux.regions.api.RegionLeaveEvent;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EntryEvents implements Listener {
    @EventHandler
    public void onRegionMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Region fromRegion = EventFunctions.getHighestPriorityRegion(from);
        Region toRegion = EventFunctions.getHighestPriorityRegion(to);

        // Prevent entering a region
        if (toRegion != null && (fromRegion == null || !fromRegion.equals(toRegion))) {
            // Check if "enter" is false
            if (!toRegion.getFlags().enter) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot enter this area.");
                return;
            }

            // Check if permission is required
            if (toRegion.getFlags().needPermissionToEnter && toRegion.getFlags().enterPermission != null) {
                if (!player.hasPermission(toRegion.getFlags().enterPermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to enter this area.");
                    return;
                }
            }

            RegionEnterEvent enterEvent = new RegionEnterEvent(player, toRegion);
            Bukkit.getPluginManager().callEvent(enterEvent);
        }

        // Prevent leaving a region
        if (fromRegion != null && (toRegion == null || !toRegion.equals(fromRegion))) {
            // Check if "leave" is false
            if (!fromRegion.getFlags().leave) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot leave this area.");
                return;
            }

            // Check if permission is required
            if (fromRegion.getFlags().needPermissionToLeave && fromRegion.getFlags().leavePermission != null) {
                if (!player.hasPermission(fromRegion.getFlags().leavePermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to leave this area.");
                }
            }

            RegionLeaveEvent leaveEvent = new RegionLeaveEvent(player, toRegion);
            Bukkit.getPluginManager().callEvent(leaveEvent);
        }
    }
}
