package lt.mredgariux.regions.utils;

import lt.mredgariux.regions.interfaces.AutoCommand;
import lt.mredgariux.regions.interfaces.PluginListener;
import lt.mredgariux.regions.main;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;

public class RegistrarCenter {
    private final Plugin plugin;
    private final main main;
    private final HashMap<String, PluginCommand> commands = new HashMap<>();
    private final HashMap<String, Listener> listeners = new HashMap<>();
    private boolean registered = false;

    public RegistrarCenter(Plugin plugin) {
        this.plugin = plugin;
        this.main = (main) plugin;
    }

    private void autoRegisterCommands() {
        Reflections reflections = new Reflections("lt.mredgariux.regions.commands");

        Set<Class<? extends AutoCommand>> classes =
                reflections.getSubTypesOf(AutoCommand.class);


        for (Class<? extends AutoCommand> clazz : classes) {
            try {
                AutoCommand cmd = clazz.getDeclaredConstructor(main.class).newInstance(main);

                String name = cmd.getName();
                PluginCommand pluginCommand = main.getCommand(name);

                if (pluginCommand != null) {
                    pluginCommand.setExecutor(cmd);
                    commands.put(name, pluginCommand);
                } else {
                    plugin.getLogger().severe("[AUTO] Nerasta plugin.yml: " + name);
                }

            } catch (Exception e) {
                plugin.getLogger().severe("[AUTO] Klaida su komanda: " + clazz.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private void autoRegisterListeners() {
        Reflections reflections = new Reflections("lt.mredgariux.regions.events");

        Set<Class<? extends PluginListener>> classes =
                reflections.getSubTypesOf(PluginListener.class);

        for (Class<? extends PluginListener> clazz : classes) {
            try {
                PluginListener listener = clazz.getDeclaredConstructor(main.class).newInstance(main);

                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                listeners.put(clazz.getSimpleName(), listener);

            } catch (Exception e) {
                plugin.getLogger().severe("[AUTO] Klaida su listener: " + clazz.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    public HashMap<String, PluginCommand> getRegisteredCommands() {
        return commands;
    }

    public HashMap<String, Listener> getRegisteredListeners() {
        return listeners;
    }

    public void registerAll() {
        if (registered) return;
        autoRegisterCommands();
        autoRegisterListeners();

        if (commands.isEmpty()) {
            plugin.getLogger().warning("[AUTO] Nei viena komanda nebuvo užregistruota");
        } else {
            String cmdList = String.join(", ", commands.keySet());
            plugin.getLogger().info("[AUTO] Užregistruotos " + commands.size() + " komandos: " + cmdList);
        }

        if (listeners.isEmpty()) {
            plugin.getLogger().warning("[AUTO] Nei vienas listener nebuvo užregistruotas");
        } else {
            String listenerList = String.join(", ", listeners.keySet());
            plugin.getLogger().info("[AUTO] Užregistruoti " + listeners.size() + " listeneriai: " + listenerList);
        }

        registered = true;
    }
}
