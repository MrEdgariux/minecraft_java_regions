package lt.mredgariux.regions.utils.expansions.chat_manager;

import lt.mredgariux.messages.chat.ChatManager;
import lt.mredgariux.regions.enums.LangKey;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NoSpamMessages {
    private static ChatManager chatManager;
    private static final HashMap<UUID, Long> sentMessagesList = new HashMap<>();

    private NoSpamMessages() {
        // Utility class; no instances needed.
    }

    public static void initialize(ChatManager manager) {
        if (chatManager != null) {
            throw new IllegalStateException("NoSpamMessages is already initialized");
        }
        chatManager = manager;
    }

    public static void shutdown() {
        sentMessagesList.clear();
        chatManager = null;
    }

    private static boolean cannotSendMessage(UUID sender, long cooldownMillis) {
        long currentTime = System.currentTimeMillis();
        if (sentMessagesList.containsKey(sender)) {
            long lastSentTime = sentMessagesList.get(sender);
            long timeSinceLastMessage = currentTime - lastSentTime;

            return timeSinceLastMessage < cooldownMillis;
        }

        if (chatManager == null) {
            throw new IllegalStateException("ChatManager is not initialized.");
        }
        return false;
    }

    /**
     * Sends a message to the player if they are not on cooldown.
     *
     * @param player         The player to send the message to.
     * @param langKey        The language key for the message.
     * @param cooldownMillis The cooldown time in milliseconds.
     */
    public static void sendMessage(Player player, LangKey langKey, long cooldownMillis) {
        if (cannotSendMessage(player.getUniqueId(), cooldownMillis)) {
            return;
        }

        chatManager.sendMessage(player, langKey);
        sentMessagesList.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Sends a message to the player if they are not on cooldown.
     *
     * @param player         The player to send the message to.
     * @param langKey        The language key for the message.
     * @param cooldownMillis The cooldown time in milliseconds.
     * @param args           The arguments for the message.
     */
    public static void sendMessage(Player player, LangKey langKey, long cooldownMillis, Object... args) {
        if (cannotSendMessage(player.getUniqueId(), cooldownMillis)) {
            return;
        }

        chatManager.sendMessage(player, langKey, args);
        sentMessagesList.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
