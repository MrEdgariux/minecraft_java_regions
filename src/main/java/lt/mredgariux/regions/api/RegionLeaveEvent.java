package lt.mredgariux.regions.api;

import lt.mredgariux.regions.classes.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Region region;

    public RegionLeaveEvent(Player player, Region region) {
        this.player = player;
        this.region = region;
    }

    public Player getPlayer() {
        return player;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
