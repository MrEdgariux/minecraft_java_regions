package lt.mredgariux.regions.commands.subcommands;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class FlagsSubcommand extends RgSubcommand {
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
            chatManager.sendMessage(sender, LangKey.REGION_LIST_HEADER, regionName);
            for (Map.Entry<RegionFlagEnum, Object> entry : flags.entrySet()) {
                Object value = "".equals(entry.getValue()) ? null : entry.getValue();
                String name = entry.getKey().name();
                String type = getType(entry.getKey().getType());
                chatManager.sendMessage(sender, LangKey.FLAGS_LIST_ENTRY, type, name, value);
            }
        } catch (Exception e) {
            chatManager.sendMessage(sender, LangKey.ERROR_MESSAGE);
            plugin.getLogger().severe(e.getMessage());
        }
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
