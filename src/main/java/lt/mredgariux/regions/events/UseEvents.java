package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class UseEvents extends PluginListener {

    public UseEvents(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location loc = event.getInteractionPoint();
        if (loc == null) return;

        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion == null) return;

        if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
            return;
        }

        boolean cancel = false;

        String blokas = block.getType().name().toLowerCase().replace("_", " ");

        if (block.getBlockData() instanceof InventoryHolder) {
            if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_CONTAINER_BLOCKS)) {
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_CONTAINER_BLOCKS_DENY_MESSAGE, config.cooldownMillis);
            }
            return;
        }


        switch (block.getType()) {
            case CRAFTING_TABLE:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_CRAFTING_TABLE)) {
                    cancel = true;
                }
                break;
            case FURNACE:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_FURNACE)) {
                    cancel = true;
                }
                break;

            case CHEST:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_CHEST)) {
                    cancel = true;
                }
                break;
            case ENDER_CHEST:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_ENDER_CHEST)) {
                    cancel = true;
                }
                break;
            case LEVER, ACACIA_BUTTON, BAMBOO_BUTTON, BIRCH_BUTTON, CHERRY_BUTTON, CRIMSON_BUTTON, DARK_OAK_BUTTON,
                 JUNGLE_BUTTON, MANGROVE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON, STONE_BUTTON, POLISHED_BLACKSTONE_BUTTON,
                 WARPED_BUTTON:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_BUTTONS)) {
                    cancel = true;
                }
                break;
            case ACACIA_PRESSURE_PLATE, BAMBOO_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, CHERRY_PRESSURE_PLATE,
                 CRIMSON_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE,
                 LIGHT_WEIGHTED_PRESSURE_PLATE, MANGROVE_PRESSURE_PLATE, OAK_PRESSURE_PLATE,
                 POLISHED_BLACKSTONE_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, STONE_PRESSURE_PLATE, WARPED_PRESSURE_PLATE:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_PRESSURE_PLATES)) {
                    cancel = true;
                }
                break;
            case CAKE:
                if (!highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.EAT_CAKE)) {
                    event.setCancelled(true);
                    NoSpamMessages.sendMessage(player, LangKey.FLAG_EAT_CAKE_DENY_MESSAGE, config.cooldownMillis);
                }
                break;
            default:
                break;
        }

        if (cancel) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_CONTAINER_BLOCKS_DENY_MESSAGE, config.cooldownMillis, blokas);
        }
    }

    @EventHandler
    public void itemFrames(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        Location loc = event.getBlock().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_ITEM_FRAMES)) {
            if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
                return;
            }
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_ITEM_FRAMES_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    @EventHandler
    public void itemFrames(HangingBreakEvent event) {
        Location loc = event.getEntity().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.USE_ITEM_FRAMES)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disableDestructionOfHanging(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        Location loc = event.getEntity().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (event.getEntity() instanceof ItemFrame) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.DESTROY_ITEM_FRAMES)) {
                if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_DESTROY_ITEM_FRAMES_DENY_MESSAGE, config.cooldownMillis);
            }
        } else if (event.getEntity() instanceof Painting) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.DESTROY_PAINTINGS)) {
                if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_DESTROY_PAINTINGS_DENY_MESSAGE, config.cooldownMillis);
            }
        }
    }

    @EventHandler
    public void disablePlacablexD(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        Location loc = event.getEntity().getLocation();
        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (event.getEntity() instanceof ItemFrame) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.DESTROY_ITEM_FRAMES)) {
                if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_DESTROY_ITEM_FRAMES_DENY_MESSAGE, config.cooldownMillis);
            }
        } else if (event.getEntity() instanceof Painting) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.DESTROY_PAINTINGS)) {
                if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                NoSpamMessages.sendMessage(player, LangKey.FLAG_DESTROY_PAINTINGS_DENY_MESSAGE, config.cooldownMillis);
            }
        }
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        Region highestPriorityRegion = regionManager.getRegionByLocation(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().getBoolean(RegionFlagEnum.EDIT_SIGNS)) {
            if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
                return;
            }
            NoSpamMessages.sendMessage(player, LangKey.FLAG_EDIT_SIGNS_DENY_MESSAGE, config.cooldownMillis);
            event.setCancelled(true);
        }
    }

}
