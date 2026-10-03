package lt.mredgariux.regions.commands.subcommands;

import lt.mredgariux.messages.text.LegacyText;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FlagsSubcommand extends RgSubcommand {
    private static final String FLAG_NAME_MARKER = "\uE000RG_FLAG_NAME\uE000";

    public FlagsSubcommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("regions.flags")) {
            chatManager.sendMessage(sender, LangKey.ERROR_NO_PERMISSION);
            return;
        }
        if (args.length == 1) {
            chatManager.sendMessage(sender, LangKey.USAGE_COMMAND_FLAGS);
            return;
        }

        try {
            String regionName = args[1].replaceAll("[^a-zA-Z0-9_\\-]", "");
            Region region = regionManager.getRegionByName(regionName);
            if (region == null) {
                chatManager.sendMessage(sender, LangKey.REGION_NOT_FOUND, regionName);
                return;
            }

            RegionFlags regionFlags = region.getFlags();
            Map<RegionFlagEnum, Object> flags = regionFlags.getFlags();
            chatManager.sendMessage(sender, LangKey.FLAGS_LIST_HEADER, regionName);
            for (Map.Entry<RegionFlagEnum, Object> entry : flags.entrySet()) {
                Object rawValue = entry.getValue();
                Object value = rawValue instanceof String[] values
                        ? Arrays.toString(values)
                        : "".equals(rawValue) ? null : rawValue;
                String name = entry.getKey().name();
                String type = getType(entry.getKey().getType());

                LangKey titleKey = LangKey.valueOf("FLAG_" + name + "_TITLE");
                LangKey descriptionKey = LangKey.valueOf("FLAG_" + name + "_DESCRIPTION");

                String title = translate(sender, titleKey);
                String description = translate(sender, descriptionKey);

                Component hover = LegacyText.parse(title)
                        .append(Component.newline())
                        .append(LegacyText.parse(description));

                String translatedEntry = translate(sender, LangKey.FLAGS_LIST_ENTRY,
                        type, FLAG_NAME_MARKER, value);
                Component entryLine = LegacyText.parse(translatedEntry).replaceText(replacement -> replacement
                        .matchLiteral(FLAG_NAME_MARKER)
                        .replacement(builder -> builder.content(name).hoverEvent(HoverEvent.showText(hover))));

                String prefix = languageManager.get(LangKey.PREFIX);
                sender.sendMessage(prefix.isBlank()
                        ? entryLine
                        : LegacyText.parse(prefix + " ").append(entryLine));
            }
        } catch (Exception e) {
            chatManager.sendMessage(sender, LangKey.ERROR_MESSAGE);
            plugin.getLogger().severe(e.getMessage());
        }
    }

    private String translate(CommandSender sender, LangKey key, Object... args) {
        return sender instanceof Player player
                ? languageManager.get(player, key, args)
                : languageManager.get(key, args);
    }

    private static String getType(Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return "List";
        } else if (type == String[].class) {
            return "List (String)";
        } else if (type == String.class) {
            return "String";
        } else if (type == Boolean.class || type == boolean.class) {
            return "Boolean";
        } else {
            return "Unknown";
        }
    }
}
