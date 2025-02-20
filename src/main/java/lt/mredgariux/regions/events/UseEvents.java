package lt.mredgariux.regions.events;

import lt.mredgariux.regions.klases.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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

        switch (block.getType()) {
            case CRAFTING_TABLE:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useCraftingTable) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use crafting table in this region.");
                }
                break;
            case FURNACE:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useFurnace) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use furnace in this region.");
                }
                break;

            case CHEST:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useChest) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use chest in this region.");
                }
                break;
            case ENDER_CHEST:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useEnderChest) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use ender chest in this region.");
                }
                break;
            case LEVER, ACACIA_BUTTON, BAMBOO_BUTTON, BIRCH_BUTTON, CHERRY_BUTTON, CRIMSON_BUTTON, DARK_OAK_BUTTON, JUNGLE_BUTTON, MANGROVE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON, STONE_BUTTON, POLISHED_BLACKSTONE_BUTTON, WARPED_BUTTON:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().useButtons) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use button in this region.");
                }
                break;
            case ACACIA_PRESSURE_PLATE, BAMBOO_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, CHERRY_PRESSURE_PLATE, CRIMSON_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, LIGHT_WEIGHTED_PRESSURE_PLATE, MANGROVE_PRESSURE_PLATE, OAK_PRESSURE_PLATE, POLISHED_BLACKSTONE_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, STONE_PRESSURE_PLATE, WARPED_PRESSURE_PLATE:
                if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().usePressurePlates) {
                    event.setCancelled(true);
                    EventFunctions.sendNoSpamMessage(player, "&cYou cannot use pressure plates in this region.");
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(loc);

        if (highestPriorityRegion != null && !highestPriorityRegion.getFlags().editSigns) {
            event.setCancelled(true);
            EventFunctions.sendNoSpamMessage(player, "&cYou cannot edit signs in this region.");
        }
    }

}
