package lt.mredgariux.regions.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.SessionManager;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.AutoCommand;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class rgCommand extends AutoCommand {
    private final WorldEdit wedit = WorldEdit.getInstance();

    public rgCommand(Plugin plugin) {
        super(plugin);
    }

    private static @NonNull String getType(Object value) {
        String type;
        if (value instanceof List<?> list) {
            type = "List";
        } else if (value instanceof String[]) {
            type = "List (String)";
        } else if (value instanceof String) {
            type = "String";
        } else if (value instanceof Boolean) {
            type = "Boolean";
        } else {
            type = "Unknown";
        }
        return type;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            chatManager.sendMessage(commandSender, LangKey.HELP_HEADER);
            chatManager.sendMessage(commandSender, LangKey.HELP_CREATE);
            chatManager.sendMessage(commandSender, LangKey.HELP_DELETE);
            chatManager.sendMessage(commandSender, LangKey.HELP_LIST);
            chatManager.sendMessage(commandSender, LangKey.HELP_FLAG);
            chatManager.sendMessage(commandSender, LangKey.HELP_FLAGS);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (!(commandSender instanceof Player player)) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_PLAYER_ONLY);
                    break;
                }

                if (!player.hasPermission("regions.create")) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_NO_PERMISSION);
                    break;
                }

                if (args.length == 1) {
                    chatManager.sendMessage(commandSender, LangKey.USAGE_COMMAND_CREATE);
                    break;
                }

                SessionManager sesija = wedit.getSessionManager();
                LocalSession sesij = sesija.findByName(player.getName());
                if (sesij == null) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_WORLD_EDIT_NO_SELECTION);
                    break;
                }
                try {
                    if (sesij.getSelection() == null) {
                        chatManager.sendMessage(commandSender, LangKey.ERROR_WORLD_EDIT_NO_SELECTION);
                        break;
                    }

                    String raw_region_name = args[1];
                    String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");

                    if (regionManager.existsRegion(region_name)) {
                        chatManager.sendMessage(commandSender, LangKey.REGION_EXISTS);
                        break;
                    }

                    CuboidRegion regionas = sesij.getSelection().getBoundingBox();
                    Location plotasStartLocation = new Location(player.getWorld(), regionas.getMinimumPoint().x(), regionas.getMinimumPoint().y(), regionas.getMinimumPoint().z());
                    Location plotasEndLocation = new Location(player.getWorld(), regionas.getMaximumPoint().x(), regionas.getMaximumPoint().y(), regionas.getMaximumPoint().z());

                    Region region = new Region(region_name, plotasStartLocation, plotasEndLocation);
                    regionManager.addRegion(region);

                    chatManager.sendMessage(commandSender, LangKey.REGION_CREATED, region_name);
                    break;
                } catch (IncompleteRegionException e) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_MESSAGE);
                    plugin.getLogger().severe(e.getMessage());
                    break;
                }
            case "flag":
                if (commandSender instanceof Player && !commandSender.hasPermission("regions.flag")) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_NO_PERMISSION);
                    break;
                }

                if (args.length < 4) {
                    chatManager.sendMessage(commandSender, LangKey.USAGE_COMMAND_FLAG);
                    break;
                }

                try {
                    String raw_region_name = args[1];
                    String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                    String flag = args[2];
                    String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                    String valueBlock = args[3];

                    Region reg = regionManager.getRegionByName(region_name);
                    if (reg == null) {
                        chatManager.sendMessage(commandSender, LangKey.REGION_NOT_FOUND, region_name);
                        break;
                    }

                    RegionFlagEnum flagEnum;

                    try {
                        flagEnum = RegionFlagEnum.valueOf(flag.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_NOT_FOUND, flag);
                        break;
                    }

                    RegionFlags regFlags = reg.getFlags();

                    if (flagEnum.getType().equals(boolean.class)) {
                        regFlags.setBoolean(flagEnum, Boolean.parseBoolean(value));
                    } else if (flagEnum.getType().equals(String.class)) {
                        if (value.equals("null") || value.equals("none")) {
                            regFlags.setString(flagEnum, "");
                        } else {
                            regFlags.setString(flagEnum, value);
                        }
                    } else if (flagEnum.getType().equals(String[].class)) {
                        List<String> values = new ArrayList<>(
                                Arrays.asList(regFlags.getStringArray(flagEnum))
                        );

                        if (args.length <= 5) {
                            chatManager.sendMessage(commandSender, LangKey.FLAGS_INVALID_OPERATION);
                            break;
                        }

                        switch (args[4].toLowerCase()) {
                            case "add" -> values.add(valueBlock);
                            case "rem" -> values.remove(valueBlock);
                            default -> chatManager.sendMessage(commandSender, LangKey.FLAGS_INVALID_OPERATION);
                        }

                        regFlags.setStringArray(flagEnum, values.toArray(String[]::new));
                    } else {
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_UNKNOWN_TYPE, flag);
                        break;
                    }

                    reg.setFlags(regFlags);

                    if (value.equals("null") || value.equals("none")) {
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_UNSET_SUCCESS, flag, region_name);
                        break;
                    }

                    if (value.contains(" ") && !value.contains("add")) {
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_UPDATED_SUCCESS, flag, region_name, value);
                    } else {
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_UPDATED_SUCCESS, flag, region_name, valueBlock);
                    }

                } catch (Exception e) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_MESSAGE);
                    plugin.getLogger().severe(e.getMessage());
                }
                break;
            case "flags":
                if (commandSender instanceof Player && !commandSender.hasPermission("regions.flags")) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_NO_PERMISSION);
                    break;
                }

                if (args.length == 1) {
                    chatManager.sendMessage(commandSender, LangKey.USAGE_COMMAND_FLAGS);
                    break;
                }

                try {
                    String raw_region_name = args[1];
                    String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                    Region reg = regionManager.getRegionByName(region_name);
                    if (reg == null) {
                        chatManager.sendMessage(commandSender, LangKey.REGION_NOT_FOUND, region_name);
                        break;
                    }

                    RegionFlags regFlags = reg.getFlags();
                    Map<RegionFlagEnum, Object> flags = regFlags.getFlags();
                    chatManager.sendMessage(commandSender, LangKey.REGION_LIST_HEADER, region_name);
                    for (Map.Entry<RegionFlagEnum, Object> entry : flags.entrySet()) {
                        Object value = entry.getValue() == "" ? null : entry.getValue();
                        String name = entry.getKey().name();
                        String type = getType(entry.getKey().getType());
                        chatManager.sendMessage(commandSender, LangKey.FLAGS_LIST_ENTRY, type, name, value);
                    }
                } catch (Exception e) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_MESSAGE);
                    plugin.getLogger().severe(e.getMessage());
                }
                break;
            case "list":
                if (commandSender instanceof Player && !commandSender.hasPermission("regions.list")) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_NO_PERMISSION);
                    break;
                }

                chatManager.sendMessage(commandSender, LangKey.REGION_LIST_HEADER);

                for (Map.Entry<String, Region> reg : regionManager.getRegions().entrySet()) {
                    Region region = reg.getValue();
                    String raw_region_name = region.getName();
                    String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                    chatManager.sendMessage(commandSender, LangKey.REGION_LIST_ENTRY, region_name);
                }

                break;
            case "delete":
                if (commandSender instanceof Player && !commandSender.hasPermission("regions.delete")) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_NO_PERMISSION);
                    break;
                }

                if (args.length == 1) {
                    chatManager.sendMessage(commandSender, LangKey.USAGE_COMMAND_DELETE);
                    break;
                }

                try {
                    String raw_region_name = args[1];
                    String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");

                    Region reg = regionManager.getRegionByName(region_name);
                    if (reg == null) {
                        chatManager.sendMessage(commandSender, LangKey.REGION_NOT_FOUND, region_name);
                        break;
                    }

                    regionManager.removeRegion(reg);
                    chatManager.sendMessage(commandSender, LangKey.REGION_DELETED, region_name);
                } catch (Exception e) {
                    chatManager.sendMessage(commandSender, LangKey.ERROR_MESSAGE);
                    plugin.getLogger().severe(e.getMessage());
                }
                break;
            default:
                chatManager.sendMessage(commandSender, LangKey.ERROR_UNKNOWN_SUBCOMMAND);
                break;
        }
        return false;
    }

    @Override
    public String getName() {
        return "rg";
    }
}
