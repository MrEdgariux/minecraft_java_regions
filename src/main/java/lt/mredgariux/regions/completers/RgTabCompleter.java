package lt.mredgariux.regions.completers;

import lt.mredgariux.regions.enums.RegionFlagEnum;
import lt.mredgariux.regions.interfaces.AutoTabCompleter;
import lt.mredgariux.regions.interfaces.PluginComponent;
import lt.mredgariux.regions.utils.BlockMaterials;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class RgTabCompleter extends PluginComponent implements AutoTabCompleter {
    public RgTabCompleter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "rg";
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return complete(Stream.of("create", "delete", "flag", "flags", "list")
                    .filter(subcommand -> canUse(sender, subcommand)), args[0]);
        }
        String subcommand = args[0].toLowerCase(Locale.ROOT);
        if (!canUse(sender, subcommand)) {
            return List.of();
        }
        if (args.length == 2 && (subcommand.equals("flag") || subcommand.equals("flags") ||
                subcommand.equals("delete"))) {
            return complete(regionManager.getRegions().keySet().stream(), args[1]);
        }
        if (!subcommand.equals("flag")) {
            return List.of();
        }
        if (args.length == 3) {
            return complete(Arrays.stream(RegionFlagEnum.values()).map(Enum::name), args[2]);
        }

        RegionFlagEnum flag;
        try {
            flag = RegionFlagEnum.valueOf(args[2].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return List.of();
        }

        if (args.length == 4) {
            if (flag.getType() == Boolean.class || flag.getType() == boolean.class) {
                return complete(Stream.of("true", "false"), args[3]);
            }
            if (flag.getType() == String[].class) {
                return complete(Stream.of("add", "rem"), args[3]);
            }
        }
        if (args.length == 5 && flag.getType() == String[].class &&
                (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("rem"))) {
            return complete(Arrays.stream(Material.values()).filter(BlockMaterials::isPlaceableBlock)
                    .map(Enum::name), args[4]);
        }
        return List.of();
    }

    private static boolean canUse(CommandSender sender, String subcommand) {
        if (subcommand.equals("create")) {
            return sender instanceof Player && sender.hasPermission("regions.create");
        }
        if (subcommand.equals("flag") || subcommand.equals("flags") ||
                subcommand.equals("delete") || subcommand.equals("list")) {
            return !(sender instanceof Player) || sender.hasPermission("regions." + subcommand);
        }
        return false;
    }

    private static List<String> complete(Stream<String> options, String prefix) {
        return options.filter(option -> option.regionMatches(true, 0, prefix, 0, prefix.length()))
                .sorted().toList();
    }
}
