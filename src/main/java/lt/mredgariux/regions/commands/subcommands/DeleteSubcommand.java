package lt.mredgariux.regions.commands.subcommands;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DeleteSubcommand extends RgSubcommand {
    public DeleteSubcommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("regions.delete")) {
            chatManager.sendMessage(sender, LangKey.ERROR_NO_PERMISSION);
            return;
        }
        if (args.length == 1) {
            chatManager.sendMessage(sender, LangKey.USAGE_COMMAND_DELETE);
            return;
        }

        try {
            String regionName = args[1].replaceAll("[^a-zA-Z0-9_\\-]", "");
            Region region = regionManager.getRegionByName(regionName);
            if (region == null) {
                chatManager.sendMessage(sender, LangKey.REGION_NOT_FOUND, regionName);
                return;
            }

            regionManager.removeRegion(region);
            chatManager.sendMessage(sender, LangKey.REGION_DELETED, regionName);
        } catch (Exception e) {
            chatManager.sendMessage(sender, LangKey.ERROR_MESSAGE);
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
