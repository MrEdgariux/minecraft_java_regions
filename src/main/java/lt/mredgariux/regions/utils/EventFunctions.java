package lt.mredgariux.regions.utils;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.main;
import lt.mredgariux.regions.messages.eng;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EventFunctions {
    private static final List<Player> noSpam = new ArrayList<>();
    static Plugin plugin = (Plugin) main.getPlugin(main.class);

    /**
     * Gets the highest priority region for a given location.
     * Priority is determined by region size (smallest region wins).
     */
    public static Region getHighestPriorityRegion(Location location) {
        Region highestPriorityRegion = null;

        for (Region reg : ((main) plugin).getRegionList().values()) {
            if (reg.containsLocation(location)) {
                if (highestPriorityRegion == null || getRegionVolume(reg) < getRegionVolume(highestPriorityRegion)) {
                    highestPriorityRegion = reg;
                }
            }
        }
        return highestPriorityRegion;
    }

    /**
     * Calculates the volume of a region.
     */
    public static int getRegionVolume(Region region) {
        int dx = Math.abs(region.getPos1().getBlockX() - region.getPos2().getBlockX()) + 1;
        int dy = Math.abs(region.getPos1().getBlockY() - region.getPos2().getBlockY()) + 1;
        int dz = Math.abs(region.getPos1().getBlockZ() - region.getPos2().getBlockZ()) + 1;
        return dx * dy * dz;
    }

    /**
     * Sends a message to the player with a cooldown to prevent spam.
     */
    public static void sendNoSpamMessage(Player player, String message) {
        if (!noSpam.contains(player)) {
            ChatManager.sendMessage(player, message, eng.prefix);
            noSpam.add(player);
            new BukkitRunnable() {
                public void run() {
                    noSpam.remove(player);
                }
            }.runTaskLater(plugin, 10);
        }
    }

    public static void sendNoSpamMessage(Player player, String message, Long duration) {
        if (!noSpam.contains(player)) {
            ChatManager.sendMessage(player, message, eng.prefix);
            noSpam.add(player);
            new BukkitRunnable() {
                public void run() {
                    noSpam.remove(player);
                }
            }.runTaskLater(plugin, duration);
        }
    }
}
