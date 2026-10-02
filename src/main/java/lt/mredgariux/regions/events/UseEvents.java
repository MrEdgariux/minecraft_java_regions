package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public class UseEvents extends PluginListener {

    public UseEvents(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK && action != Action.PHYSICAL) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) return;

        if (action != Action.PHYSICAL && event.getHand() != EquipmentSlot.HAND &&
                event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }

        RegionFlagEnum flag = interactionFlag(block, action);
        if (flag == null) return;

        Player player = event.getPlayer();
        Region highestPriorityRegion = regionManager.getRegionByLocation(block.getLocation());

        if (highestPriorityRegion == null) return;

        if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
            return;
        }

        RegionFlags flags = highestPriorityRegion.getFlags();
        if (!flags.getBoolean(flag)) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(player, denyMessage(flag), config.cooldownMillis);
        }
    }

    private static RegionFlagEnum interactionFlag(Block block, Action action) {
        Material material = block.getType();
        String name = material.name();

        if (action == Action.PHYSICAL) {
            if (name.endsWith("_PRESSURE_PLATE")) return RegionFlagEnum.USE_PRESSURE_PLATES;
            if (material == Material.TRIPWIRE) return RegionFlagEnum.USE_FUNCTIONAL_BLOCKS;
            return null;
        }
        if (action == Action.LEFT_CLICK_BLOCK) {
            return material == Material.BELL || material == Material.NOTE_BLOCK
                    ? RegionFlagEnum.USE_FUNCTIONAL_BLOCKS : null;
        }

        if (material == Material.CHEST || material == Material.TRAPPED_CHEST) return RegionFlagEnum.USE_CHEST;
        if (material == Material.FURNACE || material == Material.BLAST_FURNACE || material == Material.SMOKER) {
            return RegionFlagEnum.USE_FURNACE;
        }
        if (material == Material.ENDER_CHEST) return RegionFlagEnum.USE_ENDER_CHEST;
        if (material == Material.CRAFTING_TABLE) return RegionFlagEnum.USE_CRAFTING_TABLE;
        if (material == Material.CAKE || name.endsWith("_CANDLE_CAKE")) return RegionFlagEnum.EAT_CAKE;
        if (block.getBlockData() instanceof Switch) return RegionFlagEnum.USE_BUTTONS;
        if (name.endsWith("_SIGN")) return RegionFlagEnum.EDIT_SIGNS;
        if (isFunctionalBlock(material) || block.getBlockData() instanceof Openable) {
            return RegionFlagEnum.USE_FUNCTIONAL_BLOCKS;
        }
        if (block.getState() instanceof InventoryHolder) return RegionFlagEnum.USE_CONTAINER_BLOCKS;
        return null;
    }

    private static boolean isFunctionalBlock(Material material) {
        String name = material.name();
        if (name.endsWith("_BED") || name.endsWith("_CANDLE") || name.endsWith("_CAULDRON") ||
                name.startsWith("POTTED_")) {
            return true;
        }
        return switch (material) {
            case BEACON, NOTE_BLOCK, COMPOSTER, LOOM, JUKEBOX, BELL,
                 CARTOGRAPHY_TABLE, STONECUTTER, GRINDSTONE, SMITHING_TABLE, ENCHANTING_TABLE,
                 ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL, BREWING_STAND, LECTERN,
                 RESPAWN_ANCHOR, DAYLIGHT_DETECTOR, REPEATER, COMPARATOR,
                 FLOWER_POT, CAMPFIRE, SOUL_CAMPFIRE, BEE_NEST, BEEHIVE,
                 LODESTONE, TRIAL_SPAWNER, VAULT, SUSPICIOUS_SAND, SUSPICIOUS_GRAVEL,
                 COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK,
                 STRUCTURE_BLOCK, JIGSAW, SPAWNER, END_PORTAL_FRAME, DRAGON_EGG -> true;
            default -> false;
        };
    }

    private static LangKey denyMessage(RegionFlagEnum flag) {
        return switch (flag) {
            case USE_PRESSURE_PLATES -> LangKey.FLAG_USE_PRESSURE_PLATES_DENY_MESSAGE;
            case USE_BUTTONS -> LangKey.FLAG_USE_BUTTONS_DENY_MESSAGE;
            case USE_CHEST -> LangKey.FLAG_USE_CHEST_DENY_MESSAGE;
            case USE_FURNACE -> LangKey.FLAG_USE_FURNACE_DENY_MESSAGE;
            case USE_CRAFTING_TABLE -> LangKey.FLAG_USE_CRAFTING_TABLE_DENY_MESSAGE;
            case USE_ENDER_CHEST -> LangKey.FLAG_USE_ENDER_CHEST_DENY_MESSAGE;
            case USE_CONTAINER_BLOCKS -> LangKey.FLAG_USE_CONTAINER_BLOCKS_DENY_MESSAGE;
            case EAT_CAKE -> LangKey.FLAG_EAT_CAKE_DENY_MESSAGE;
            case EDIT_SIGNS -> LangKey.FLAG_EDIT_SIGNS_DENY_MESSAGE;
            default -> LangKey.FLAG_USE_FUNCTIONAL_BLOCKS_DENY_MESSAGE;
        };
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
