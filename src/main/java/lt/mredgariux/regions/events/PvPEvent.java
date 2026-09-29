package lt.mredgariux.regions.events;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class PvPEvent extends PluginListener {
    private final Map<UUID, UUID> cloudToThrowerMap = new HashMap<>();

    public PvPEvent(Plugin plugin) {
        super(plugin);
    }

    /**
     * Handles direct and indirect player-caused damage to other players
     */
    @EventHandler
    public void onPvPEvent(EntityDamageByEntityEvent event) {
        // Get the true damage source (handles projectiles and other indirect damage)
        Player damager = getPlayerDamager(event);

        // If no player is responsible, or the entity damaged isn't a player, return
        if (damager == null || !(event.getEntity() instanceof Player entityDamaged)) {
            return;
        }

        // Check if either player is in a protected region
        Region damagerRegion = regionManager.getRegionByLocation(damager.getLocation());
        Region victimRegion = regionManager.getRegionByLocation(entityDamaged.getLocation());

        // Cancel the event if either region has pvp disabled
        if ((damagerRegion != null && !damagerRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) ||
                (victimRegion != null && !victimRegion.getFlags().getBoolean(RegionFlagEnum.PVP))) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(damager, LangKey.FLAG_PVP_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    /**
     * Handles splash potions which can be used for PvP
     */
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        // Check if a player threw the potion
        if (!(event.getEntity().getShooter() instanceof Player thrower)) {
            return;
        }

        // Check if the thrower is in a protected region
        Region throwerRegion = regionManager.getRegionByLocation(thrower.getLocation());
        Region thrownAtRegion = regionManager.getRegionByLocation(event.getPotion().getLocation());

        if ((throwerRegion != null && !throwerRegion.getFlags().getBoolean(RegionFlagEnum.USE_THROWABLE_POTIONS)) || (thrownAtRegion != null && !thrownAtRegion.getFlags().getBoolean(RegionFlagEnum.USE_THROWABLE_POTIONS))) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(thrower, LangKey.FLAG_USE_THROWABLE_POTIONS_DENY_MESSAGE, config.cooldownMillis);
            return;
        }

        // For each affected entity that's a player
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof Player victim && victim != thrower) {
                // Check if the victim is in a protected region
                Region victimRegion = regionManager.getRegionByLocation(victim.getLocation());

                // If either region has pvp disabled
                if ((throwerRegion != null && !throwerRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) ||
                        (victimRegion != null && !victimRegion.getFlags().getBoolean(RegionFlagEnum.PVP))) {
                    for (PotionEffect type : event.getPotion().getEffects()) {
                        if (isHarmfulEffect(type.getType())) {
                            // Set intensity to 0 for this player
                            event.setIntensity(victim, 0);
                            NoSpamMessages.sendMessage(thrower, LangKey.FLAG_PVP_DENY_MESSAGE, config.cooldownMillis);
                            return;
                        }
                    }
                }
            }
        });
    }

    /**
     * Handles lingering potions which create area effect clouds
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        // Check if a player threw the lingering potion
        if (!(event.getEntity().getShooter() instanceof Player thrower)) {
            return;
        }

        // Check if the thrower is in a protected region
        Region throwerRegion = regionManager.getRegionByLocation(thrower.getLocation());
        Region cloudRegion = regionManager.getRegionByLocation(event.getAreaEffectCloud().getLocation());

        if ((throwerRegion != null && !throwerRegion.getFlags().getBoolean(RegionFlagEnum.USE_THROWABLE_POTIONS)) ||
                (cloudRegion != null && !cloudRegion.getFlags().getBoolean(RegionFlagEnum.USE_THROWABLE_POTIONS))) {
            NoSpamMessages.sendMessage(thrower, LangKey.FLAG_USE_THROWABLE_POTIONS_DENY_MESSAGE, config.cooldownMillis);
            event.setCancelled(true);
            return;
        }

        // If either region has pvp disabled
        if ((throwerRegion != null && !throwerRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) ||
                (cloudRegion != null && !cloudRegion.getFlags().getBoolean(RegionFlagEnum.PVP))) {
            // Store the cloud's UUID mapped to the thrower's UUID for future reference
            cloudToThrowerMap.put(event.getAreaEffectCloud().getUniqueId(), thrower.getUniqueId());

            // Make the cloud harmless by modifying its properties
            // This is more reliable than setting the source to null
            AreaEffectCloud cloud = event.getAreaEffectCloud();

            // Store original potion effects
            Collection<PotionEffect> originalEffects = new ArrayList<>(cloud.getCustomEffects());

            // Remove harmful effects but keep beneficial ones
            cloud.clearCustomEffects();
            for (PotionEffect effect : originalEffects) {
                // Re-add only beneficial effects (healing, regen, etc.)
                if (isHarmfulEffect(effect.getType())) {
                    continue; // Skip harmful effects
                }
                cloud.addCustomEffect(effect, true);
            }
            NoSpamMessages.sendMessage(thrower, LangKey.FLAG_PVP_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    private boolean isHarmfulEffect(PotionEffectType type) {
        // List of harmful effect types that should be blocked in no-pvp regions
        return type == PotionEffectType.INSTANT_DAMAGE
                || type == PotionEffectType.POISON
                || type == PotionEffectType.WITHER
                || type == PotionEffectType.WEAKNESS
                || type == PotionEffectType.SLOWNESS
                || type == PotionEffectType.BLINDNESS
                || type == PotionEffectType.NAUSEA
                || type == PotionEffectType.HUNGER
                || type == PotionEffectType.UNLUCK;
    }

    /**
     * Handles area effect clouds (from lingering potions) when they apply effects
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        Player thrower;

        // Check if we have this cloud in our map
        if (cloudToThrowerMap.containsKey(cloud.getUniqueId())) {
            UUID throwerUUID = cloudToThrowerMap.get(cloud.getUniqueId());
            thrower = Bukkit.getPlayer(throwerUUID);
        }
        // If not in our map, try to get from the cloud's source
        else if (cloud.getSource() instanceof Player) {
            thrower = (Player) cloud.getSource();
            // Add to our map for future ticks
            cloudToThrowerMap.put(cloud.getUniqueId(), thrower.getUniqueId());
        } else {
            thrower = null;
        }

        // If there's no thrower, or it's not a player, we can't attribute it to PvP
        if (thrower == null) {
            return;
        }

        // Check region at cloud location
        Region cloudRegion = regionManager.getRegionByLocation(cloud.getLocation());

        // Filter affected entities, removing players that are in protected regions
        event.getAffectedEntities().removeIf(entity -> {
            if (entity instanceof Player victim && !victim.getUniqueId().equals(thrower.getUniqueId())) {
                Region victimRegion = regionManager.getRegionByLocation(victim.getLocation());

                // If either region has pvp disabled
                if ((cloudRegion != null && !cloudRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) ||
                        (victimRegion != null && !victimRegion.getFlags().getBoolean(RegionFlagEnum.PVP))) {
                    // This event can fire multiple times, so don't spam
                    NoSpamMessages.sendMessage(thrower, LangKey.FLAG_PVP_DENY_MESSAGE, config.cooldownMillis);
                    return true; // Remove this entity from affected entities
                }
            }
            return false;
        });
    }

    /**
     * Handles fire spread caused by players (fire arrows, flint and steel, etc.)
     */
    @EventHandler
    public void onEntityCombust(EntityCombustByEntityEvent event) {
        // Get the true combuster (handles fire arrows and other indirect fire)
        Player combuster = getPlayerDamager(event);

        // If no player is responsible, or the entity combusted isn't a player, return
        if (combuster == null || !(event.getEntity() instanceof Player entityCombust)) {
            return;
        }

        // Check if either player is in a protected region
        Region combusterRegion = regionManager.getRegionByLocation(combuster.getLocation());
        Region victimRegion = regionManager.getRegionByLocation(entityCombust.getLocation());

        // Cancel the event if either region has pvp disabled
        if ((combusterRegion != null && !combusterRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) ||
                (victimRegion != null && !victimRegion.getFlags().getBoolean(RegionFlagEnum.PVP))) {
            event.setCancelled(true);
            NoSpamMessages.sendMessage(combuster, LangKey.FLAG_PVP_DENY_MESSAGE, config.cooldownMillis);
        }
    }

    /**
     * Handles all damage to players
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player entityDamaged) {
            Region victimRegion = regionManager.getRegionByLocation(entityDamaged.getLocation());

            if (victimRegion != null && !victimRegion.getFlags().getBoolean(RegionFlagEnum.PVP)) {
                // If it's direct PvP damage, let the more specific handler above handle it
                if (event instanceof EntityDamageByEntityEvent) {
                    return;
                }

                // For environmental damage, cancel it based on region settings
                event.setCancelled(true);
            }
        }
    }

    /**
     * Helper method to get the player responsible for damage, handling projectiles and other indirect sources
     */
    private Player getPlayerDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }

    /**
     * Helper method for combustion events that works like getPlayerDamager
     */
    private Player getPlayerDamager(EntityCombustByEntityEvent event) {
        if (event.getCombuster() instanceof Player) {
            return (Player) event.getCombuster();
        } else if (event.getCombuster() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }
}