package networkcore;

import com.google.gson.Gson;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

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
        if (!event.getIdentifier().getId().equals(CHANNEL)) return;
        if (!(event.getSource() instanceof Player player)) return;

        String json = new String(event.getData(), StandardCharsets.UTF_8);

        Map<String, Object> msg = gson.fromJson(json, Map.class);
        String pluginChannel = (String) msg.get("plugin");

        if (pluginChannel != null && PluginMessageRouter.isPluginRegistered(pluginChannel)) {
            PluginMessageRouter.sendMessage(pluginChannel, json);
        }
    }
}
