package lt.mredgariux.regions.commands.subcommands;

import lt.mredgariux.regions.classes.Region;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ListSubcommand extends RgSubcommand {
    public ListSubcommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("regions.list")) {
            chatManager.sendMessage(sender, LangKey.ERROR_NO_PERMISSION);
            return;
        }

        chatManager.sendMessage(sender, LangKey.REGION_LIST_HEADER);
        for (Region region : regionManager.getRegions().values()) {
            String regionName = region.getName().replaceAll("[^a-zA-Z0-9_\\-]", "");
            chatManager.sendMessage(sender, LangKey.REGION_LIST_ENTRY, regionName);
        }
    }
}
