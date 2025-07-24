package networkcore.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NetworkBalanceHandler implements java.util.function.Consumer<String> {

    private final ProxyServer proxyServer;
    private final Gson gson = new Gson();
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("networkbalance", "core");

    public NetworkBalanceHandler(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void accept(String jsonMessage) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> msg = gson.fromJson(jsonMessage, mapType);

        String type = (String) msg.get("type");
        String fromName = (String) msg.get("from");
        String toName = (String) msg.get("to");
        Double amount = msg.containsKey("amount") ? ((Number) msg.get("amount")).doubleValue() : null;

        switch (type) {
            case "PAY_REQUEST" -> {
                Player target = proxyServer.getPlayer(toName).orElse(null);
                if (target == null) return;
                sendPluginMessage(target, jsonMessage);
            }

            case "PAY_CONFIRM" -> {
                Player sender = proxyServer.getPlayer(fromName).orElse(null);
                if (sender == null) return;
                sendPluginMessage(sender, jsonMessage);
            }

            default -> {
                // Unknown type, optionally log
            }
        }
    }

    private void sendPluginMessage(Player player, String json) {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        player.sendPluginMessage(CHANNEL, data);
    }
}
