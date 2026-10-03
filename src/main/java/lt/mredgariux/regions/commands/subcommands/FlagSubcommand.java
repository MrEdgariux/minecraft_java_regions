package lt.mredgariux.regions.commands.subcommands;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import lt.mredgariux.regions.utils.BlockMaterials;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlagSubcommand extends RgSubcommand {
    public FlagSubcommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("regions.flag")) {
            chatManager.sendMessage(sender, LangKey.ERROR_NO_PERMISSION);
            return;
        }
        if (args.length < 4) {
            chatManager.sendMessage(sender, LangKey.USAGE_COMMAND_FLAG);
            return;
        }

        try {
            String regionName = args[1].replaceAll("[^a-zA-Z0-9_\\-]", "");
            String flag = args[2];
            Region region = regionManager.getRegionByName(regionName);
            if (region == null) {
                chatManager.sendMessage(sender, LangKey.REGION_NOT_FOUND, regionName);
                return;
            }

            RegionFlagEnum flagEnum;
            try {
                flagEnum = RegionFlagEnum.valueOf(flag.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                chatManager.sendMessage(sender, LangKey.FLAGS_NOT_FOUND, flag);
                return;
            }

            RegionFlags regionFlags = region.getFlags();
            Class<?> type = flagEnum.getType();
            if (type == Boolean.class || type == boolean.class) {
                if (args.length != 4 || !isBooleanValue(args[3])) {
                    chatManager.sendMessage(sender, LangKey.FLAGS_INVALID_VALUE, flag);
                    return;
                }
                regionFlags.setBoolean(flagEnum, Boolean.parseBoolean(args[3]));
            } else if (type == String.class) {
                String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                if (value.equalsIgnoreCase("null") || value.equalsIgnoreCase("none")) {
                    regionFlags.setString(flagEnum, "");
                    chatManager.sendMessage(sender, LangKey.FLAGS_UNSET_SUCCESS, flag, regionName);
                } else {
                    regionFlags.setString(flagEnum, value);
                    chatManager.sendMessage(sender, LangKey.FLAGS_UPDATED_SUCCESS, flag, regionName, value);
                }
            } else if (type == String[].class) {
                if (args.length != 5 || !(args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("rem"))) {
                    chatManager.sendMessage(sender, LangKey.FLAGS_INVALID_OPERATION);
                    return;
                }

                Material material = Material.matchMaterial(args[4]);
                if (material == null || !BlockMaterials.isPlaceableBlock(material)) {
                    chatManager.sendMessage(sender, LangKey.FLAGS_INVALID_VALUE, flag);
                    return;
                }

                List<String> values = new ArrayList<>(Arrays.asList(regionFlags.getStringArray(flagEnum)));
                if (args[3].equalsIgnoreCase("add")) {
                    if (!values.contains(material.name())) {
                        values.add(material.name());
                    }
                } else {
                    values.remove(material.name());
                }
                regionFlags.setStringArray(flagEnum, values.toArray(String[]::new));
                chatManager.sendMessage(sender, LangKey.FLAGS_UPDATED_SUCCESS, flag, regionName, material.name());
            } else {
                chatManager.sendMessage(sender, LangKey.FLAGS_UNKNOWN_TYPE, flag);
                return;
            }

            region.setFlags(regionFlags);
            if (type == Boolean.class || type == boolean.class) {
                chatManager.sendMessage(sender, LangKey.FLAGS_UPDATED_SUCCESS, flag, regionName, args[3]);
            }
        } catch (Exception e) {
            chatManager.sendMessage(sender, LangKey.ERROR_MESSAGE);
            plugin.getLogger().severe(e.getMessage());
        }
    }

    private static boolean isBooleanValue(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }
}
