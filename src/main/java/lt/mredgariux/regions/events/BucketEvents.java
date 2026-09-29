package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.plugin.Plugin;

public class BucketEvents extends PluginListener {
    public BucketEvents(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion == null || player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
            return;
        }

        RegionFlags flags = highestPriorityRegion.getFlags();
        if (!flags.getBoolean(RegionFlagEnum.USE_BUCKETS)) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_BUCKETS_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion == null || player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
            return;
        }

        RegionFlags flags = highestPriorityRegion.getFlags();
        if (!flags.getBoolean(RegionFlagEnum.USE_BUCKETS)) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_BUCKETS_DENY_MESSAGE, config.cooldownMillis);
        }
    }
}
