package lt.mredgariux.regions.events;

import lt.mredgariux.regions.api.RegionEnterEvent;
import lt.mredgariux.regions.api.RegionLeaveEvent;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

public class EntryEvents implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
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
                if (player.hasPermission("regions.bypass.enter." + toRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot enter this area. Caused by: &6" + event.getCause().name());
                return;
            }

            // Check if permission is required
            if (toRegion.getFlags().needPermissionToEnter && !Objects.equals(toRegion.getFlags().enterPermission, "")) {
                if (!player.hasPermission(toRegion.getFlags().enterPermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to enter this area. Caused by: &6" + event.getCause().name());
                    return;
                }
            }

            RegionEnterEvent enterEvent = new RegionEnterEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(enterEvent);
        }

        // Prevent leaving a region
        if (fromRegion != null && (toRegion == null || !toRegion.equals(fromRegion))) {
            // Check if "leave" is false
            if (!fromRegion.getFlags().leave) {
                if (player.hasPermission("regions.bypass.leave." + fromRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot leave this area. Caused by: &6" + event.getCause().name());
                return;
            }

            // Check if permission is required
            if (fromRegion.getFlags().needPermissionToLeave && !Objects.equals(fromRegion.getFlags().leavePermission, "")) {
                if (!player.hasPermission(fromRegion.getFlags().leavePermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to leave this area. Caused by: &6" + event.getCause().name());
                }
            }

            RegionLeaveEvent leaveEvent = new RegionLeaveEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(leaveEvent);
        }
    }
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
                if (player.hasPermission("regions.bypass.enter." + toRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot enter this area.");
                return;
            }

            // Check if permission is required
            if (toRegion.getFlags().needPermissionToEnter && !Objects.equals(toRegion.getFlags().enterPermission, "")) {
                if (!player.hasPermission(toRegion.getFlags().enterPermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to enter this area.");
                    return;
                }
            }

            if (!Objects.equals(toRegion.getFlags().enterMessage, "")) {
                Title title;
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    String message = PlaceholderAPI.setPlaceholders(player, toRegion.getFlags().enterMessage);
                    title = Title.title(Component.text(""), Component.text(message, NamedTextColor.GOLD));
                } else {
                    title = Title.title(Component.text(""), Component.text(toRegion.getFlags().enterMessage, NamedTextColor.GOLD));
                }

                player.showTitle(title);
            }

            RegionEnterEvent enterEvent = new RegionEnterEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(enterEvent);
        }

        // Prevent leaving a region
        if (fromRegion != null && (toRegion == null || !toRegion.equals(fromRegion))) {
            // Check if "leave" is false
            if (!fromRegion.getFlags().leave) {
                if (player.hasPermission("regions.bypass.leave." + fromRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot leave this area.");
                return;
            }

            // Check if permission is required
            if (fromRegion.getFlags().needPermissionToLeave && !Objects.equals(fromRegion.getFlags().leavePermission, "")) {
                if (!player.hasPermission(fromRegion.getFlags().leavePermission)) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou don't have permission to leave this area.");
                }
            }

            if (!Objects.equals(fromRegion.getFlags().leaveMessage, "")) {
                Title title;
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    String message = PlaceholderAPI.setPlaceholders(player, fromRegion.getFlags().leaveMessage);
                    title = Title.title(Component.text(""), Component.text(message, NamedTextColor.GOLD));
                } else {
                    title = Title.title(Component.text(""), Component.text(fromRegion.getFlags().leaveMessage, NamedTextColor.GOLD));
                }

                player.showTitle(title);
            }

            RegionLeaveEvent leaveEvent = new RegionLeaveEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(leaveEvent);
        }
    }
}
