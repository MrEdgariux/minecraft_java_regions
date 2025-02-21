package lt.mredgariux.regions.api;

import lt.mredgariux.regions.classes.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegionEnterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Region fromRegion;
    private final Region toRegion;

    public RegionEnterEvent(Player player, Region fromRegion, Region toRegion) {
        this.player = player;
        this.fromRegion = fromRegion;
        this.toRegion = toRegion;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @Nullable Region getFromRegion() {
        return fromRegion;
    }

    public @Nullable Region getToRegion() {
        return toRegion;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
