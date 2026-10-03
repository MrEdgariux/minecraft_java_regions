package lt.mredgariux.regions.commands.subcommands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CreateSubcommand extends RgSubcommand {
    public CreateSubcommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            chatManager.sendMessage(sender, LangKey.ERROR_PLAYER_ONLY);
            return;
        }
        if (!player.hasPermission("regions.create")) {
            chatManager.sendMessage(sender, LangKey.ERROR_NO_PERMISSION);
            return;
        }
        if (args.length == 1) {
            chatManager.sendMessage(sender, LangKey.USAGE_COMMAND_CREATE);
            return;
        }

        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
        if (session == null) {
            chatManager.sendMessage(sender, LangKey.ERROR_WORLD_EDIT_NO_SELECTION);
            return;
        }
        try {
            if (session.getSelection() == null) {
                chatManager.sendMessage(sender, LangKey.ERROR_WORLD_EDIT_NO_SELECTION);
                return;
            }

            String regionName = args[1].replaceAll("[^a-zA-Z0-9_\\-]", "");
            if (regionManager.existsRegion(regionName)) {
                chatManager.sendMessage(sender, LangKey.REGION_EXISTS);
                return;
            }

            CuboidRegion selection = session.getSelection().getBoundingBox();
            Location start = new Location(player.getWorld(), selection.getMinimumPoint().x(),
                    selection.getMinimumPoint().y(), selection.getMinimumPoint().z());
            Location end = new Location(player.getWorld(), selection.getMaximumPoint().x(),
                    selection.getMaximumPoint().y(), selection.getMaximumPoint().z());
            regionManager.addRegion(new Region(regionName, start, end));
            chatManager.sendMessage(sender, LangKey.REGION_CREATED, regionName);
        } catch (IncompleteRegionException e) {
            chatManager.sendMessage(sender, LangKey.ERROR_WORLD_EDIT_NO_SELECTION);
        }
    }
}
