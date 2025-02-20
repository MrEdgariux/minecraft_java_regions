package lt.mredgariux.regions.events;

import lt.mredgariux.regions.klases.Region;
import lt.mredgariux.regions.utils.EventFunctions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPEvent implements Listener {
    @EventHandler
    public void onPvPEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player entityDamaged) {
            Region highestPriorityRegion = EventFunctions.getHighestPriorityRegion(damager.getLocation());
            Region highestPriorityRegion2 = EventFunctions.getHighestPriorityRegion(entityDamaged.getLocation());

            if ((highestPriorityRegion != null && !highestPriorityRegion.getFlags().pvp) || (highestPriorityRegion2 != null && !highestPriorityRegion2.getFlags().pvp)) {
                event.setCancelled(true);
                EventFunctions.sendNoSpamMessage(damager, "&cYou cannot pvp in this region.");
            }
        }
    }
}
