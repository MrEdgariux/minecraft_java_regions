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
import lt.mredgariux.regions.utils.LanguageManager;
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
    public final LanguageManager lang;

    public rgCommand(Plugin plugin) {
        this.plugin = plugin;
        this.database = ((main) plugin).getDatabase();

        lang = ((main) plugin).getLang();
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
                    if (((main) plugin).reloadPlugin()) {
                        ChatManager.sendMessage(player, lang, "plugin-reloaded");
                    } else {
                        ChatManager.sendMessage(player, lang, "plugin-reload-failed");
                    }
                    break;
                case "create":
                    if (!player.hasPermission("regions.create")) {
                        ChatManager.sendMessage(player, lang, "no-permission");
                        break;
                    }

                    if (args.length == 1) {
                        ChatManager.sendMessage(player, lang, "usage-command-create");
                        break;
                    }

                    SessionManager sesija = wedit.getSessionManager();
                    LocalSession sesij = sesija.findByName(player.getName());
                    if (sesij == null) {
                        ChatManager.sendMessage(player, lang, "error-worldedit-no-selection");
                        break;
                    }
                    try {
                        if (sesij.getSelection() == null) {
                            ChatManager.sendMessage(player, lang, "error-worldedit-no-selection");
                            break;
                        }

                        String raw_region_name = args[1];
                        String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");

                        if (database.existRegion(region_name)) {
                            ChatManager.sendMessage(player, lang, "region-exists", region_name);
                            break;
                        }

                        CuboidRegion regionas = sesij.getSelection().getBoundingBox();
                        Location plotasStartLocation = new Location(player.getWorld(), regionas.getMinimumPoint().x(), regionas.getMinimumPoint().y(), regionas.getMinimumPoint().z());
                        Location plotasEndLocation = new Location(player.getWorld(), regionas.getMaximumPoint().x(), regionas.getMaximumPoint().y(), regionas.getMaximumPoint().z());

                        Region region = new Region(region_name, plotasStartLocation, plotasEndLocation, player.getUniqueId());
                        ((main) plugin).addRegion(region);

                        database.insertRegion(region_name, plotasStartLocation, plotasEndLocation, player.getUniqueId().toString(), region.getFlags());
                        ChatManager.sendMessage(player, lang, "region-created", region_name);
                        break;
                    } catch (IncompleteRegionException e) {
                        ChatManager.sendMessage(player, lang, "error-message");
                        plugin.getLogger().severe(e.getMessage());
                        break;
                    }
                case "flag":
                    if (!player.hasPermission("regions.flag")) {
                        ChatManager.sendMessage(player, lang, "no-permission");
                        break;
                    }

                    if (args.length < 4) {
                        ChatManager.sendMessage(player, lang, "usage-command-flag");
                        break;
                    }

                    try {
                        String raw_region_name = args[1];
                        String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                        String flag = args[2];
                        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                        String valueBlock = args[3];

                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, lang, "region-not-found", region_name);
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
                                List<String> flagList = (List<String>) list;

                                if (args.length > 4 && args[4].equalsIgnoreCase("add")) {
                                    flagList.add(valueBlock);
                                } else if (args.length > 4 && args[4].equalsIgnoreCase("rem")) {
                                    flagList.remove(valueBlock);
                                } else {
                                    ChatManager.sendMessage(player, lang, "flags-invalid-operation");
                                    break;
                                }
                                field.set(regFlags, flagList);
                            } else {
                                ChatManager.sendMessage(player, lang, "flags-not-list");
                                break;
                            }
                        } else {
                            ChatManager.sendMessage(player, lang, "flags-unknown-type", flag);
                            break;
                        }
                        reg.setFlags(regFlags);
                        ((main) plugin).updateRegion(reg);
                        database.updateRegionFlags(region_name, regFlags);
                        if (value.equals("null") || value.equals("none")) {
                            ChatManager.sendMessage(player, lang, "flags-unset-success", flag, region_name);
                            break;
                        }
                        if (value.contains(" ") && !value.contains("add")) {
                            ChatManager.sendMessage(player, lang, "flags-updated-success", flag, region_name, value);
                        } else {
                            ChatManager.sendMessage(player, lang, "flags-updated-success", flag, region_name, valueBlock);
                        }

                    } catch (Exception e) {
                        ChatManager.sendMessage(player, lang, "error-message");
                        plugin.getLogger().severe(e.getMessage());
                    }
                    break;
                case "flags":
                    if (!player.hasPermission("regions.flags")) {
                        ChatManager.sendMessage(player, lang, "no-permission");
                        break;
                    }

                    if (args.length == 1) {
                        ChatManager.sendMessage(player, lang, "usage-command-flags");
                        break;
                    }

                    try {
                        String raw_region_name = args[1];
                        String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, lang, "region-not-found", region_name);
                            break;
                        }

                        RegionFlags regFlags = reg.getFlags();
                        ChatManager.sendMessage(player, lang, "flags-list-header", region_name);
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
                                ChatManager.sendMessage(player, lang, "error-message");
                                plugin.getLogger().severe(e.getMessage());
                                break;
                            }
                        }
                    } catch (Exception e) {
                        ChatManager.sendMessage(player, lang, "error-message");
                        plugin.getLogger().severe(e.getMessage());
                    }
                    break;
                case "list":
                    if (!player.hasPermission("regions.list")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    ChatManager.sendMessage(player, lang, "region-list-header");

                    for (Map.Entry<String, Region> reg : ((main) plugin).getRegionList().entrySet()) {
                        Region region = reg.getValue();
                        String raw_region_name = region.getName();
                        String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
                        ChatManager.sendMessage(player, String.format(
                                "&7 - &6%s &7- &6Owned by: %s",
                                region_name,
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
                        ChatManager.sendMessage(player, lang, "usage-command-delete");
                        break;
                    }

                    try {
                        String raw_region_name = args[1];
                        String region_name = raw_region_name.replaceAll("[^a-zA-Z0-9_\\-]", "");

                        Region reg = database.getRegion(region_name);
                        if (!database.existRegion(region_name) || reg == null) {
                            ChatManager.sendMessage(player, lang, "region-not-found", region_name);
                            break;
                        }

                        database.deleteRegion(region_name);
                        ((main) plugin).removeRegion(reg);
                        ChatManager.sendMessage(player, lang, "region-deleted", region_name);
                    } catch (Exception e) {
                        ChatManager.sendMessage(player, lang, "error-message");
                        plugin.getLogger().severe(e.getMessage());
                    }
                    break;
                default:
                    ChatManager.sendMessage(player, lang, "unknown-subcommand");
                    break;
            }
            return false;
        } else {
            ChatManager.sendMessage(player, lang, "help-header");
            ChatManager.sendMessage(player, lang, "help-create");
            ChatManager.sendMessage(player, lang, "help-delete");
            ChatManager.sendMessage(player, lang, "help-list");
            ChatManager.sendMessage(player, lang, "help-flag");
            ChatManager.sendMessage(player, lang, "help-flags");
        }
        return false;
    }
}
