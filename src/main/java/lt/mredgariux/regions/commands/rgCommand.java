package lt.mredgariux.regions.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.SessionManager;
import lt.mredgariux.regions.databases.Database;
import lt.mredgariux.regions.klases.Region;
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

import java.awt.*;

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

                    if (args.length < 2) {
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

                        String region_name = args[2];

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
                case "list":
                    if (!player.hasPermission("regions.list")) {
                        ChatManager.sendMessage(player, eng.commands_rg_no_permission, eng.prefix);
                        break;
                    }

                    ChatManager.sendMessage(player, "&cList of regions:", eng.prefix);
                    for (Region reg : ((main) plugin).getRegionList()) {
                        ChatManager.sendMessage(player, String.format("&7 - &6%s &7- &6Owned by: %s", reg.getName(), Bukkit.getOfflinePlayer(reg.getOwner()).getName()), eng.prefix);
                    }
                    break;
                case "delete":
                    ChatManager.sendMessage(player, "&cDelete command not implemented yet!", eng.prefix);
                    break;
                default:
                    ChatManager.sendMessage(player, "&cUnknown subcommand!", eng.prefix);
                    break;
            }
            return false;
        }
        return false;
    }
}
