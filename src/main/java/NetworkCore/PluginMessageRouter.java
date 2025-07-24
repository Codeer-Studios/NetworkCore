package networkcore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PluginMessageRouter {

    private static final Map<String, Consumer<String>> pluginHandlers = new HashMap<>();

    public static void registerPlugin(String channel, Consumer<String> handler) {
        pluginHandlers.put(channel, handler);
    }

    public static void sendMessage(String channel, String message) {
        Consumer<String> handler = pluginHandlers.get(channel);
        if (handler != null) {
            handler.accept(message);
        }
    }

    public static boolean isPluginRegistered(String channel) {
        return pluginHandlers.containsKey(channel);
    }
}
