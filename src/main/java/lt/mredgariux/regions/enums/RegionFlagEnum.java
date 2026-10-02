package lt.mredgariux.regions.enums;

public enum RegionFlagEnum {
    BLOCKS_PLACE(Boolean.class, false),
    BLOCKS_PLACE_SPECIFIC(String[].class, new String[]{}),

    BLOCKS_BREAK(Boolean.class, false),
    BLOCKS_BREAK_SPECIFIC(String[].class, new String[]{}),

    DESTROY_PAINTINGS(Boolean.class, false),
    DESTROY_ITEM_FRAMES(Boolean.class, false),

    EAT_CAKE(Boolean.class, false),

    PVP(Boolean.class, false),
    TNT(Boolean.class, false),
    ENDER_DRAGON_DESTROY_BLOCKS(Boolean.class, false),
    EDIT_SIGNS(Boolean.class, false),

    USE_PRESSURE_PLATES(Boolean.class, false),
    USE_BUTTONS(Boolean.class, false),
    USE_CHEST(Boolean.class, false),
    USE_FURNACE(Boolean.class, false),
    USE_CRAFTING_TABLE(Boolean.class, false),
    USE_ENDER_CHEST(Boolean.class, false),
    USE_CONTAINER_BLOCKS(Boolean.class, false),
    USE_ITEM_FRAMES(Boolean.class, false),
    USE_BUCKETS(Boolean.class, false),
    USE_WORLD_EDIT(Boolean.class, false),
    USE_THROWABLE_POTIONS(Boolean.class, false),

    FIRE_SPREAD(Boolean.class, false),

    ALLOW_ENTER(Boolean.class, true),
    ENTER_PERMISSION(String.class, ""),
    ENTER_MESSAGE(String.class, ""),

    ALLOW_LEAVE(Boolean.class, true),
    LEAVE_PERMISSION(String.class, ""),
    LEAVE_MESSAGE(String.class, "");

    private final Class<?> type;
    private final Object defaultValue;

    RegionFlagEnum(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}