package lt.mredgariux.regions.enums;

import lt.mredgariux.messages.language.LanguageKey;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LangKey implements LanguageKey {
    PREFIX("prefix"),

    ERROR_NO_PERMISSION("error-no-permission"),
    ERROR_PLAYER_ONLY("error-player-only"),
    ERROR_MESSAGE("error-message"),
    ERROR_WORLD_EDIT_NO_SELECTION("error-world-edit-no-selection"),
    ERROR_UNKNOWN_SUBCOMMAND("error-unknown-subcommand"),

    PLUGIN_RELOADED("plugin-reloaded"),
    PLUGIN_RELOAD_FAILED("plugin-reload-failed"),

    HELP_HEADER("help-header"),
    HELP_CREATE("help-create"),
    HELP_DELETE("help-delete"),
    HELP_LIST("help-list"),
    HELP_FLAG("help-flag"),
    HELP_FLAGS("help-flags"),

    // Flags (Oh crap, that's really a lot of language keys that I'll need to add)

    FLAG_BLOCKS_PLACE_TITLE("flag-blocks-place-title"),
    FLAG_BLOCKS_PLACE_DESCRIPTION("flag-blocks-place-description"),
    FLAG_BLOCKS_PLACE_DENY_MESSAGE("flag-blocks-place-deny-message"),

    FLAG_BLOCKS_PLACE_SPECIFIC_TITLE("flag-blocks-place-specific-title"),
    FLAG_BLOCKS_PLACE_SPECIFIC_DESCRIPTION("flag-blocks-place-specific-description"),
    FLAG_BLOCKS_PLACE_SPECIFIC_DENY_MESSAGE("flag-blocks-place-specific-deny-message"),

    FLAG_BLOCKS_BREAK_TITLE("flag-blocks-break-title"),
    FLAG_BLOCKS_BREAK_DESCRIPTION("flag-blocks-break-description"),
    FLAG_BLOCKS_BREAK_DENY_MESSAGE("flag-blocks-break-deny-message"),

    FLAG_BLOCKS_BREAK_SPECIFIC_TITLE("flag-blocks-break-specific-title"),
    FLAG_BLOCKS_BREAK_SPECIFIC_DESCRIPTION("flag-blocks-break-specific-description"),
    FLAG_BLOCKS_BREAK_SPECIFIC_DENY_MESSAGE("flag-blocks-break-specific-deny-message"),

    FLAG_DESTROY_PAINTINGS_TITLE("flag-destroy-paintings-title"),
    FLAG_DESTROY_PAINTINGS_DESCRIPTION("flag-destroy-paintings-description"),
    FLAG_DESTROY_PAINTINGS_DENY_MESSAGE("flag-destroy-paintings-deny-message"),

    FLAG_DESTROY_ITEM_FRAMES_TITLE("flag-destroy-item-frames-title"),
    FLAG_DESTROY_ITEM_FRAMES_DESCRIPTION("flag-destroy-item-frames-description"),
    FLAG_DESTROY_ITEM_FRAMES_DENY_MESSAGE("flag-destroy-item-frames-deny-message"),

    FLAG_EAT_CAKE_TITLE("flag-eat-cake-title"),
    FLAG_EAT_CAKE_DESCRIPTION("flag-eat-cake-description"),
    FLAG_EAT_CAKE_DENY_MESSAGE("flag-eat-cake-deny-message"),

    FLAG_PVP_TITLE("flag-pvp-title"),
    FLAG_PVP_DESCRIPTION("flag-pvp-description"),
    FLAG_PVP_DENY_MESSAGE("flag-pvp-deny-message"),

    FLAG_TNT_TITLE("flag-tnt-title"),
    FLAG_TNT_DESCRIPTION("flag-tnt-description"),
    FLAG_TNT_DENY_MESSAGE("flag-tnt-deny-message"),

    FLAG_ENDER_DRAGON_DESTROY_BLOCKS_TITLE("flag-ender-dragon-destroy-blocks-title"),
    FLAG_ENDER_DRAGON_DESTROY_BLOCKS_DESCRIPTION("flag-ender-dragon-destroy-blocks-description"),
    FLAG_ENDER_DRAGON_DESTROY_BLOCKS_DENY_MESSAGE("flag-ender-dragon-destroy-blocks-deny-message"),

    FLAG_EDIT_SIGNS_TITLE("flag-edit-signs-title"),
    FLAG_EDIT_SIGNS_DESCRIPTION("flag-edit-signs-description"),
    FLAG_EDIT_SIGNS_DENY_MESSAGE("flag-edit-signs-deny-message"),

    FLAG_USE_PRESSURE_PLATES_TITLE("flag-use-pressure-plates-title"),
    FLAG_USE_PRESSURE_PLATES_DESCRIPTION("flag-use-pressure-plates-description"),
    FLAG_USE_PRESSURE_PLATES_DENY_MESSAGE("flag-use-pressure-plates-deny-message"),

    FLAG_USE_BUTTONS_TITLE("flag-use-buttons-title"),
    FLAG_USE_BUTTONS_DESCRIPTION("flag-use-buttons-description"),
    FLAG_USE_BUTTONS_DENY_MESSAGE("flag-use-buttons-deny-message"),

    FLAG_USE_CHEST_TITLE("flag-use-chest-title"),
    FLAG_USE_CHEST_DESCRIPTION("flag-use-chest-description"),
    FLAG_USE_CHEST_DENY_MESSAGE("flag-use-chest-deny-message"),

    FLAG_USE_FURNACE_TITLE("flag-use-furnace-title"),
    FLAG_USE_FURNACE_DESCRIPTION("flag-use-furnace-description"),
    FLAG_USE_FURNACE_DENY_MESSAGE("flag-use-furnace-deny-message"),

    FLAG_USE_CRAFTING_TABLE_TITLE("flag-use-crafting-table-title"),
    FLAG_USE_CRAFTING_TABLE_DESCRIPTION("flag-use-crafting-table-description"),
    FLAG_USE_CRAFTING_TABLE_DENY_MESSAGE("flag-use-crafting-table-deny-message"),

    FLAG_USE_ENDER_CHEST_TITLE("flag-use-ender-chest-title"),
    FLAG_USE_ENDER_CHEST_DESCRIPTION("flag-use-ender-chest-description"),
    FLAG_USE_ENDER_CHEST_DENY_MESSAGE("flag-use-ender-chest-deny-message"),

    FLAG_USE_CONTAINER_BLOCKS_TITLE("flag-use-container-blocks-title"),
    FLAG_USE_CONTAINER_BLOCKS_DESCRIPTION("flag-use-container-blocks-description"),
    FLAG_USE_CONTAINER_BLOCKS_DENY_MESSAGE("flag-use-container-blocks-deny-message"),

    FLAG_USE_ITEM_FRAMES_TITLE("flag-use-item-frames-title"),
    FLAG_USE_ITEM_FRAMES_DESCRIPTION("flag-use-item-frames-description"),
    FLAG_USE_ITEM_FRAMES_DENY_MESSAGE("flag-use-item-frames-deny-message"),

    FLAG_USE_BUCKETS_TITLE("flag-use-buckets-title"),
    FLAG_USE_BUCKETS_DESCRIPTION("flag-use-buckets-description"),
    FLAG_USE_BUCKETS_DENY_MESSAGE("flag-use-buckets-deny-message"),

    FLAG_USE_WORLD_EDIT_TITLE("flag-use-world-edit-title"),
    FLAG_USE_WORLD_EDIT_DESCRIPTION("flag-use-world-edit-description"),
    FLAG_USE_WORLD_EDIT_DENY_MESSAGE("flag-use-world-edit-deny-message"),

    FLAG_USE_THROWABLE_POTIONS_TITLE("flag-use-throwable-potions-title"),
    FLAG_USE_THROWABLE_POTIONS_DESCRIPTION("flag-use-throwable-potions-description"),
    FLAG_USE_THROWABLE_POTIONS_DENY_MESSAGE("flag-use-throwable-potions-deny-message"),

    FLAG_FIRE_SPREAD_TITLE("flag-fire-spread-title"),
    FLAG_FIRE_SPREAD_DESCRIPTION("flag-fire-spread-description"),
    FLAG_FIRE_SPREAD_DENY_MESSAGE("flag-fire-spread-deny-message"),

    FLAG_ALLOW_ENTER_TITLE("flag-allow-enter-title"),
    FLAG_ALLOW_ENTER_DESCRIPTION("flag-allow-enter-description"),
    FLAG_ALLOW_ENTER_DENY_MESSAGE("flag-allow-enter-deny-message"),

    FLAG_ENTER_PERMISSION_TITLE("flag-enter-permission-title"),
    FLAG_ENTER_PERMISSION_DESCRIPTION("flag-enter-permission-description"),
    FLAG_ENTER_PERMISSION_DENY_MESSAGE("flag-enter-permission-deny-message"),

    FLAG_ENTER_MESSAGE_TITLE("flag-enter-message-title"),
    FLAG_ENTER_MESSAGE_DESCRIPTION("flag-enter-message-description"),
    FLAG_ENTER_MESSAGE_DENY_MESSAGE("flag-enter-message-deny-message"),

    FLAG_ALLOW_LEAVE_TITLE("flag-allow-leave-title"),
    FLAG_ALLOW_LEAVE_DESCRIPTION("flag-allow-leave-description"),
    FLAG_ALLOW_LEAVE_DENY_MESSAGE("flag-allow-leave-deny-message"),

    FLAG_LEAVE_PERMISSION_TITLE("flag-leave-permission-title"),
    FLAG_LEAVE_PERMISSION_DESCRIPTION("flag-leave-permission-description"),
    FLAG_LEAVE_PERMISSION_DENY_MESSAGE("flag-leave-permission-deny-message"),

    FLAG_LEAVE_MESSAGE_TITLE("flag-leave-message-title"),
    FLAG_LEAVE_MESSAGE_DESCRIPTION("flag-leave-message-description"),
    FLAG_LEAVE_MESSAGE_DENY_MESSAGE("flag-leave-message-deny-message"),

    REGION_CREATED("region-created"),
    REGION_DELETED("region-deleted"),
    REGION_NOT_FOUND("region-not-found"),
    REGION_EXISTS("region-exists"),
    REGION_LIST_HEADER("region-list-header"),
    REGION_LIST_ENTRY("region-list-entry"),

    FLAGS_NOT_LIST("flags-not-list"),
    FLAGS_INVALID_OPERATION("flags-invalid-operation"),
    FLAGS_INVALID_VALUE("flags-invalid-value"),
    FLAGS_UNKNOWN_TYPE("flags-unknown-type"),
    FLAGS_UNSET_SUCCESS("flags-unset-success"),
    FLAGS_UPDATED_SUCCESS("flags-updated-success"),
    FLAGS_LIST_HEADER("flags-list-header"),
    FLAGS_LIST_ENTRY("flags-list-entry"),
    FLAGS_NOT_FOUND("flags-not-found"),

    USAGE_COMMAND_CREATE("usage-command-create"),
    USAGE_COMMAND_FLAG("usage-command-flag"),
    USAGE_COMMAND_FLAGS("usage-command-flags"),
    USAGE_COMMAND_DELETE("usage-command-delete");

    private static final Map<String, LangKey> BY_KEY = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(
                    LangKey::key,
                    Function.identity()
            ));
    private final String key;


    LangKey(String key) {
        this.key = key;
    }

    public static LangKey fromKey(String key) {
        return BY_KEY.get(key);
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}