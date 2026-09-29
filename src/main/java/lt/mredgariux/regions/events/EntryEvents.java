package lt.mredgariux.regions.events;

import lt.mredgariux.regions.api.RegionEnterEvent;
import lt.mredgariux.regions.api.RegionLeaveEvent;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class EntryEvents extends PluginListener {

    public EntryEvents(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Region fromRegion = regionManager.getRegionByLocation(from);
        Region toRegion = regionManager.getRegionByLocation(to);

        // Prevent entering a region
        if (toRegion != null && (fromRegion == null || !fromRegion.equals(toRegion))) {
            // Check if "enter" is false
            if (!toRegion.getFlags().getBoolean(RegionFlagEnum.ALLOW_ENTER) && !player.hasPermission("regions.bypass.enter." + toRegion.getName())) {
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_ALLOW_ENTER_DENY_MESSAGE, config.cooldownMillis);
                return;
            }

            // Check if permission is required
            if (!Objects.equals(toRegion.getFlags().getString(RegionFlagEnum.ENTER_PERMISSION), "")) {
                if (!player.hasPermission(toRegion.getFlags().getString(RegionFlagEnum.ENTER_PERMISSION))) {
                    event.setCancelled(true);
                    NoSpamMessages.sendMessage(player, LangKey.FLAG_ENTER_PERMISSION_DENY_MESSAGE, config.cooldownMillis);
                    return;
                }
            }

            RegionEnterEvent enterEvent = new RegionEnterEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(enterEvent);
        }

        // Prevent leaving a region
        if (fromRegion != null && (toRegion == null || !toRegion.equals(fromRegion))) {
            // Check if "leave" is false
            if (!fromRegion.getFlags().getBoolean(RegionFlagEnum.ALLOW_LEAVE)) {
                if (player.hasPermission("regions.bypass.leave." + fromRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_ALLOW_LEAVE_DENY_MESSAGE, config.cooldownMillis);
                return;
            }

            // Check if permission is required
            if (!Objects.equals(fromRegion.getFlags().getString(RegionFlagEnum.LEAVE_PERMISSION), "")) {
                if (!player.hasPermission(fromRegion.getFlags().getString(RegionFlagEnum.LEAVE_PERMISSION))) {
                    event.setCancelled(true);
                    NoSpamMessages.sendMessage(player, LangKey.FLAG_LEAVE_PERMISSION_DENY_MESSAGE, config.cooldownMillis);
                    return;
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

        Region fromRegion = regionManager.getRegionByLocation(from);
        Region toRegion = regionManager.getRegionByLocation(to);

        // Prevent entering a region
        if (toRegion != null && (fromRegion == null || !fromRegion.equals(toRegion))) {
            // Check if "enter" is false
            if (!toRegion.getFlags().getBoolean(RegionFlagEnum.ALLOW_ENTER)) {
                if (player.hasPermission("regions.bypass.enter." + toRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_ALLOW_ENTER_DENY_MESSAGE, config.cooldownMillis);
                return;
            }

            // Check if permission is required
            if (!Objects.equals(toRegion.getFlags().getString(RegionFlagEnum.ENTER_PERMISSION), "")) {
                if (!player.hasPermission(toRegion.getFlags().getString(RegionFlagEnum.ENTER_PERMISSION))) {
                    event.setCancelled(true);
                    NoSpamMessages.sendMessage(player, LangKey.FLAG_ENTER_PERMISSION_DENY_MESSAGE, config.cooldownMillis);
                    return;
                }
            }

            RegionEnterEvent enterEvent = new RegionEnterEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(enterEvent);
        }

        // Prevent leaving a region
        if (fromRegion != null && (toRegion == null || !toRegion.equals(fromRegion))) {
            // Check if "leave" is false
            if (!fromRegion.getFlags().getBoolean(RegionFlagEnum.ALLOW_LEAVE)) {
                if (player.hasPermission("regions.bypass.leave." + fromRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_ALLOW_LEAVE_DENY_MESSAGE, config.cooldownMillis);
                return;
            }

            // Check if permission is required
            if (!Objects.equals(fromRegion.getFlags().getString(RegionFlagEnum.LEAVE_PERMISSION), "")) {
                if (!player.hasPermission(fromRegion.getFlags().getString(RegionFlagEnum.LEAVE_PERMISSION))) {
                    event.setCancelled(true);
                    NoSpamMessages.sendMessage(player, LangKey.FLAG_LEAVE_PERMISSION_DENY_MESSAGE, config.cooldownMillis);
                    return;
                }
            }

            RegionLeaveEvent leaveEvent = new RegionLeaveEvent(player, fromRegion, toRegion);
            Bukkit.getPluginManager().callEvent(leaveEvent);
        }
    }
}
