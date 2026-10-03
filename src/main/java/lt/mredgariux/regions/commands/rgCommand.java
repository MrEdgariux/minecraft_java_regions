package lt.mredgariux.regions.commands;

import lt.mredgariux.regions.commands.subcommands.CreateSubcommand;
import lt.mredgariux.regions.commands.subcommands.DeleteSubcommand;
import lt.mredgariux.regions.commands.subcommands.FlagSubcommand;
import lt.mredgariux.regions.commands.subcommands.FlagsSubcommand;
import lt.mredgariux.regions.commands.subcommands.ListSubcommand;
import lt.mredgariux.regions.interfaces.RgSubcommand;
import lt.mredgariux.regions.enums.LangKey;
import lt.mredgariux.regions.interfaces.AutoCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class rgCommand extends AutoCommand {
    private final Map<String, RgSubcommand> subcommands;

    public rgCommand(Plugin plugin) {
        super(plugin);
        subcommands = Map.of(
                "create", new CreateSubcommand(plugin),
                "delete", new DeleteSubcommand(plugin),
                "flag", new FlagSubcommand(plugin),
                "flags", new FlagsSubcommand(plugin),
                "list", new ListSubcommand(plugin)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            chatManager.sendMessage(sender, LangKey.HELP_HEADER);
            chatManager.sendMessage(sender, LangKey.HELP_CREATE);
            chatManager.sendMessage(sender, LangKey.HELP_DELETE);
            chatManager.sendMessage(sender, LangKey.HELP_LIST);
            chatManager.sendMessage(sender, LangKey.HELP_FLAG);
            chatManager.sendMessage(sender, LangKey.HELP_FLAGS);
            return true;
        }

        RgSubcommand subcommand = subcommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subcommand == null) {
            chatManager.sendMessage(sender, LangKey.ERROR_UNKNOWN_SUBCOMMAND);
        } else {
            subcommand.execute(sender, args);
        }
        return true;
    }

    @Override
    public String getName() {
        return "rg";
    }
}
