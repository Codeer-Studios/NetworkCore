package networkcore;

import com.google.gson.Gson;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class VelocityPluginMessageListener {

    private static final String CHANNEL = "network:core";
    private final Gson gson = new Gson();
    private final ProxyServer server;

    public VelocityPluginMessageListener(ProxyServer server) {
        this.server = server;

    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        System.out.println("[VelocityPluginMessageListener] PluginMessageEvent triggered");

        String channelId = event.getIdentifier().getId();
        System.out.println("[VelocityPluginMessageListener] Channel ID: " + channelId);

        if (!channelId.equals(CHANNEL)) {
            System.out.println("[VelocityPluginMessageListener] Channel ID does NOT match expected '" + CHANNEL + "', ignoring message");
            return;
        }
        System.out.println("[VelocityPluginMessageListener] Channel ID matches expected channel");

        // Accept Player or ServerConnection as source
        Object source = event.getSource();
        if (source instanceof Player player) {
            System.out.println("[VelocityPluginMessageListener] Event source is Player: " + player.getUsername());
        } else if (source instanceof ServerConnection serverConnection) {
            System.out.println("[VelocityPluginMessageListener] Event source is ServerConnection: " + serverConnection.getServerInfo().getName());
        } else {
            System.out.println("[VelocityPluginMessageListener] Event source is neither Player nor ServerConnection, ignoring message");
            return;
        }

        String json = new String(event.getData(), StandardCharsets.UTF_8);
        System.out.println("[VelocityPluginMessageListener] Received plugin message JSON: " + json);

        Map<String, Object> msg;
        try {
            msg = gson.fromJson(json, Map.class);
        } catch (Exception e) {
            System.out.println("[VelocityPluginMessageListener] Failed to parse JSON: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (msg == null) {
            System.out.println("[VelocityPluginMessageListener] Parsed message is null");
            return;
        }

        String pluginChannel = (String) msg.get("plugin");
        System.out.println("[VelocityPluginMessageListener] Plugin channel from message: " + pluginChannel);

        if (pluginChannel != null && PluginMessageRouter.isPluginRegistered(pluginChannel)) {
            System.out.println("[VelocityPluginMessageListener] Plugin channel is registered, forwarding message");
            PluginMessageRouter.sendMessage(pluginChannel, json);
        } else {
            System.out.println("[VelocityPluginMessageListener] Plugin channel is NOT registered or is null");
        }
    }
}
