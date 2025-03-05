package lt.mredgariux.regions.events;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public class UseEvents implements Listener {

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

        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion == null) return;

        if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
            return;
        }

        if (block.getBlockData() instanceof InventoryHolder) {
            if (!highestPriorityRegion.getFlags().useContainerBlocks) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + block.getType().name().toLowerCase().replace("_", " ") + " in this region.");
            }
            return;
        }

        String blokas = block.getType().name().toLowerCase().replace("_", " ");

        switch (block.getType()) {
            case CRAFTING_TABLE:
                if (!highestPriorityRegion.getFlags().useCraftingTable) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;
            case FURNACE:
                if (!highestPriorityRegion.getFlags().useFurnace) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;

            case CHEST:
                if (!highestPriorityRegion.getFlags().useChest) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;
            case ENDER_CHEST:
                if (!highestPriorityRegion.getFlags().useEnderChest) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;
            case LEVER, ACACIA_BUTTON, BAMBOO_BUTTON, BIRCH_BUTTON, CHERRY_BUTTON, CRIMSON_BUTTON, DARK_OAK_BUTTON, JUNGLE_BUTTON, MANGROVE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON, STONE_BUTTON, POLISHED_BLACKSTONE_BUTTON, WARPED_BUTTON:
                if (!highestPriorityRegion.getFlags().useButtons) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;
            case ACACIA_PRESSURE_PLATE, BAMBOO_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, CHERRY_PRESSURE_PLATE, CRIMSON_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, LIGHT_WEIGHTED_PRESSURE_PLATE, MANGROVE_PRESSURE_PLATE, OAK_PRESSURE_PLATE, POLISHED_BLACKSTONE_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, STONE_PRESSURE_PLATE, WARPED_PRESSURE_PLATE:
                if (!highestPriorityRegion.getFlags().usePressurePlates) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use " + blokas + " in this region.");
                }
                break;
            case CAKE:
                if (!highestPriorityRegion.getFlags().eatCake) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot eat " + blokas + " in this region.");
                }
                break;
            default:
                break;

        }
    }

    @EventHandler
    public void itemFrames(PlayerItemFrameChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getItemFrame().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useItemFrames) {
            if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
                return;
            }
            event.setCancelled(true);
            EventFunctions.sendNoSpamMessage(player, "&cYou cannot modify item frames in this region.");
        }
    }

    @EventHandler
    public void disableDestructionOfHanging(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        Location loc = event.getEntity().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (event.getEntity() instanceof ItemFrame) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().destroyItemFrames) {
                if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot destroy item frames in this region.");
            }
        } else if (event.getEntity() instanceof Painting) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().destroyPaintings) {
                if (player.hasPermission("regions.bypass.break." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot destroy paintings in this region.");
            }
        }
    }

    @EventHandler
    public void disablePlacablexD(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        Location loc = event.getEntity().getLocation();
        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (event.getEntity() instanceof ItemFrame) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().destroyItemFrames) {
                if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot place item frames in this region.");
            }
        } else if (event.getEntity() instanceof Painting) {
            if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().destroyPaintings) {
                if (player.hasPermission("regions.bypass.build." + highestPriorityRegion.getName())) {
                    return;
                }
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(player, "&cYou cannot place paintings in this region.");
            }
        }
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().editSigns) {
            if (player.hasPermission("regions.bypass.use." + highestPriorityRegion.getName())) {
                return;
            }
            event.setCancelled(true);
            EventFunctions.sendNoSpamMessage(player, "&cYou cannot edit signs in this region.");
        }
    }

}
