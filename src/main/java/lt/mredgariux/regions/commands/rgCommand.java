package lt.mredgariux.regions.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.SessionManager;
import lt.mredgariux.regions.databases.Database;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.classes.RegionFlags;
import lt.mredgariux.regions.main;
import lt.mredgariux.regions.messages.eng;
import lt.mredgariux.regions.utils.ChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class rgCommand implements CommandExecutor {
    private final WorldEdit wedit = WorldEdit.getInstance();
    private final Plugin plugin;
    public final Database database;

    public rgCommand(Plugin plugin) {
        this.plugin = plugin;
        this.database = ((main) plugin).getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Component.text("You must be a player to use this command (for now)", NamedTextColor.RED));
            return false;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    ChatManager.sendMessage(player, "&cNot implemented yet!", eng.prefix);
                    break;
                case "create":
                    if (!player.hasPermission("regions.create")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    if (args.length == 1) {
                        ChatManager.sendMessage(player, "&cUsage: /region create <name>", eng.prefix);
                        break;
                    }

                    SessionManager sesija = wedit.getSessionManager();
                    LocalSession sesij = sesija.findByName(player.getName());
                    if (sesij == null) {
                        ChatManager.sendMessage(player, "&cSelect a region with WorldEdit to use this command.", eng.prefix);
                        break;
                    }
                    try {
                        if (sesij.getSelection() == null) {
                            ChatManager.sendMessage(player, "&cSelect a region with WorldEdit to use this command.", eng.prefix);
                            break;
                        }

                        String region_name = args[1];

                        if (database.existRegion(region_name)) {
                            ChatManager.sendMessage(player, "&cRegion with this name already exists", eng.prefix);
                            break;
                        }

                        CuboidRegion regionas = sesij.getSelection().getBoundingBox();
                        Location plotasStartLocation = new Location(player.getWorld(), regionas.getMinimumPoint().x(), regionas.getMinimumPoint().y(), regionas.getMinimumPoint().z());
                        Location plotasEndLocation = new Location(player.getWorld(), regionas.getMaximumPoint().x(), regionas.getMaximumPoint().y(), regionas.getMaximumPoint().z());

                        Region region = new Region(region_name, plotasStartLocation, plotasEndLocation, player.getUniqueId());
                        ((main) plugin).addRegion(region);

                        database.insertRegion(region_name, plotasStartLocation, plotasEndLocation, player.getUniqueId().toString(), region.getFlags());
                        ChatManager.sendMessage(player, "&aRegion " + region_name + " created successfully!", eng.prefix);
                        break;
                    } catch (IncompleteRegionException e) {
                        ChatManager.sendMessage(player, "&cUnknown region error occurred", eng.prefix);
                        plugin.getLogger().severe(e.getMessage());
                        break;
                    }
                case "flag":
                    if (!player.hasPermission("regions.flag")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    if (args.length < 4) {
                        ChatManager.sendMessage(player, "&cUsage: /region flag <name> <flag> <value> [add/rem]", eng.prefix);
                        break;
                    }

                    try {
                        String region_name = args[1];
                        String flag = args[2];
                        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                        String valueBlock = args[3];

                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, "&cRegion " + region_name + " does not exist", eng.prefix);
                            break;
                        }

                        RegionFlags regFlags = reg.getFlags();

                        Field field = RegionFlags.class.getDeclaredField(flag);
                        field.setAccessible(true);

                        if (field.getType().equals(boolean.class)) {
                            field.set(regFlags, Boolean.parseBoolean(value));
                        } else if (field.getType().equals(String.class)) {
                            if (value.equals("null") || value.equals("none")) {
                                field.set(regFlags, "");
                            } else {
                                field.set(regFlags, value);
                            }
                        } else if (field.getType().equals(List.class)) {
                            Object fieldValue = field.get(regFlags);
                            if (fieldValue instanceof List<?> list) {
                                List<String> flagList = (List<String>) list; // Kastingas į List<String>

                                if (args.length > 4 && args[4].equalsIgnoreCase("add")) {
                                    flagList.add(valueBlock);  // pridėti naują elementą į sąrašą
                                } else if (args.length > 4 && args[4].equalsIgnoreCase("rem")) {
                                    flagList.remove(valueBlock);  // pridėti naują elementą į sąrašą
                                } else {
                                    ChatManager.sendMessage(player, "&cInvalid operation for List flag (add/rem)", eng.prefix);
                                    break;
                                }
                                field.set(regFlags, flagList);  // nustatyti atnaujintą sąrašą atgal
                            } else {
                                ChatManager.sendMessage(player, "&cFlag is not a List", eng.prefix);
                                break;
                            }
                        } else {
                            ChatManager.sendMessage(player, "&cUnknown field type", eng.prefix);
                            break;
                        }
                        reg.setFlags(regFlags);
                        ((main) plugin).updateRegion(reg);
                        database.updateRegionFlags(region_name, regFlags);
                        if (value.equals("null") || value.equals("none")) {
                            ChatManager.sendMessage(player, "&cFlag " + flag + " for " + region_name + " deleted successfully", eng.prefix);
                            break;
                        }
                        if (value.contains(" ") && !value.contains("add")) {
                            ChatManager.sendMessage(player, "&aFlag " + flag + " for " + region_name + " updated to " + value, eng.prefix);
                        } else {
                            ChatManager.sendMessage(player, "&aFlag " + flag + " for " + region_name + " updated to " + valueBlock, eng.prefix);
                        }

                    } catch (Exception e) {
                        ChatManager.sendMessage(player, "&cUnknown flag provided", eng.prefix);
                    }
                    break;
                case "flags":
                    if (!player.hasPermission("regions.flags")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    if (args.length == 1) {
                        ChatManager.sendMessage(player, "&cUsage: /region flags <name>", eng.prefix);
                        break;
                    }

                    try {
                        String region_name = args[1];
                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, "&cRegion " + region_name + " does not exist", eng.prefix);
                            break;
                        }

                        RegionFlags regFlags = reg.getFlags();
                        ChatManager.sendMessage(player, "&aRegion " + region_name + " flags:", eng.prefix);
                        for (Field field : RegionFlags.class.getDeclaredFields()) {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(regFlags) == "" ? null : field.get(regFlags);
                                String name = field.getName();
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
                                ChatManager.sendMessage(player, "&8 - &6Flag Type&8:&6 " + type + " &8- &6" + name + "&8:&6 " + value, eng.prefix);
                            } catch (IllegalAccessException e) {
                                plugin.getLogger().severe(e.getMessage());
                                ChatManager.sendMessage(player, "&cSomething gone wrong while getting flags...", eng.prefix);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        plugin.getLogger().severe(e.getMessage());
                        ChatManager.sendMessage(player, "&cSomething gone wrong here...", eng.prefix);
                    }
                    break;
                case "list":
                    if (!player.hasPermission("regions.list")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    ChatManager.sendMessage(player, "&cList of regions:", eng.prefix);

                    for (Map.Entry<String, Region> reg : ((main) plugin).getRegionList().entrySet()) {
                        Region region = reg.getValue();
                        ChatManager.sendMessage(player, String.format(
                                "&7 - &6%s &7- &6Owned by: %s",
                                region.getName(),
                                Bukkit.getOfflinePlayer(region.getOwner()).getName()
                        ), eng.prefix);
                    }

                    break;
                case "delete":
                    if (!player.hasPermission("regions.delete")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }
                    if (args.length == 1) {
                        ChatManager.sendMessage(player, "&cUsage: /region delete <name>", eng.prefix);
                        break;
                    }

                    try {
                        String region_name = args[1];
                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, "&cRegion " + region_name + " does not exist", eng.prefix);
                            break;
                        }

                        database.deleteRegion(region_name);
                        ((main) plugin).removeRegion(reg);
                    } catch (Exception e) {
                        plugin.getLogger().severe(e.getMessage());
                        ChatManager.sendMessage(player, "&cSomething gone wrong here...", eng.prefix);
                    }
                    break;
                default:
                    ChatManager.sendMessage(player, "&cUnknown subcommand!", eng.prefix);
                    break;
            }
            return false;
        } else {
            ChatManager.sendMessage(player, "&aRegions - &6Re-Developed by &cMrEdgariux!", eng.prefix);
            ChatManager.sendMessage(player, "&7- &6/rg create <name>", eng.prefix);
            ChatManager.sendMessage(player, "&7- &6/rg list", eng.prefix);
            ChatManager.sendMessage(player, "&7- &6/rg flag <name> <flag> <value>", eng.prefix);
            ChatManager.sendMessage(player, "&7- &6/rg flags <name>", eng.prefix);
            ChatManager.sendMessage(player, "&7- &6/rg delete <name>", eng.prefix);
        }
        return false;
    }
}
