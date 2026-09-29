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
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BuildingEvent extends PluginListener {

    public BuildingEvent(Plugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region region = regionManager.getRegionByLocation(loc);

        if (region == null || player.hasPermission("regions.bypass.build." + region.getName())) {
            return;
        }

        RegionFlags flags = region.getFlags();
        List<String> allowedBlocks = flags.getStringList(RegionFlagEnum.BLOCKS_PLACE_SPECIFIC);

        if (!allowedBlocks.isEmpty() && allowedBlocks.contains(event.getBlock().getType().name())) {
            return;
        }
        if (!flags.getBoolean(RegionFlagEnum.BLOCKS_PLACE)) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_BLOCKS_PLACE_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        Region region = regionManager.getRegionByLocation(loc);

        if (region == null || player.hasPermission("regions.bypass.build." + region.getName())) {
            return;
        }
        RegionFlags flags = region.getFlags();
        List<String> disallowedBlocks = flags.getStringList(RegionFlagEnum.BLOCKS_BREAK_SPECIFIC);

        if (!disallowedBlocks.isEmpty() && disallowedBlocks.contains(event.getBlock().getType().name())) {
            return;
        }

        if (!flags.getBoolean(RegionFlagEnum.BLOCKS_BREAK)) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_BLOCKS_BREAK_DENY_MESSAGE, config.cooldownMillis);
        }
    }
}
