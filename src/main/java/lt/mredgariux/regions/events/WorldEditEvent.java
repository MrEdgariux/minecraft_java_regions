package lt.mredgariux.regions.events;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.PluginComponent;
import lt.mredgariux.regions.utils.expansions.chat_manager.NoSpamMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WorldEditEvent extends PluginComponent {
    public WorldEditEvent(Plugin plugin) {
        super(plugin);
        try {
            WorldEdit.getInstance().getEventBus().register(new Object() {
                @Subscribe
                public void onEditSessionEvent(EditSessionEvent event) {
                    if (event.getActor() != null) {
                        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {

                            public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 pos, T block) throws WorldEditException {

                                if (event.getActor() == null) return getExtent().setBlock(pos, block);

                                Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(event.getWorld()).getName()), pos.x(), pos.y(), pos.z());
                                Region region = regionManager.getRegionByLocation(loc);
                                Player player = Bukkit.getPlayer(event.getActor().getUniqueId());

                                if (player == null) return getExtent().setBlock(pos, block);

                                if (region != null && !region.getFlags().getBoolean(RegionFlagEnum.USE_WORLD_EDIT)) {
                                    if (player.hasPermission("regions.bypass.we." + region.getName())) {
                                        return getExtent().setBlock(pos, block); // Allowing use of the worldedit
                                    }
                                    NoSpamMessages.sendMessage(player, LangKey.FLAG_USE_WORLD_EDIT_DENY_MESSAGE, config.cooldownMillis);
                                    return false;
                                }

                                return getExtent().setBlock(pos, block);
                            }

                        });
                    }
                }
            });
        } catch (NoClassDefFoundError ignored) {
            plugin.getLogger().warning("WorldEdit is not installed, WorldEdit protection will not work. Except it must be installed otherwise plugin won't work, so how is that event erroring?? :<");
        }
    }
}
