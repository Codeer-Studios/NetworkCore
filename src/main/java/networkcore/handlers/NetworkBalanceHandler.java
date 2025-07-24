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
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("network", "core");

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

        System.out.println("[NetworkBalanceHandler] Received message: " + jsonMessage);
        System.out.println("[NetworkBalanceHandler] Type: " + type + ", From: " + fromName + ", To: " + toName);

        switch (type) {
            case "PAY_REQUEST" -> {
                Player target = proxyServer.getPlayer(toName).orElse(null);
                if (target == null) {
                    System.out.println("[NetworkBalanceHandler] Target player not found on proxy: " + toName);
                    return;
                }
                System.out.println("[NetworkBalanceHandler] Sending PAY_REQUEST to " + toName + "'s server");
                sendPluginMessage(target, jsonMessage);
                break;
            }

            case "PAY_CONFIRM" -> {
                Player sender = proxyServer.getPlayer(fromName).orElse(null);
                if (sender == null) {
                    System.out.println("[NetworkBalanceHandler] Sender player not found on proxy: " + fromName);
                    return;
                }
                System.out.println("[NetworkBalanceHandler] Sending PAY_CONFIRM to " + fromName + "'s server");
                sendPluginMessage(sender, jsonMessage);
                break;
            }

            default -> {
                // Unknown type, optionally log
            }
        }
    }

    private void sendPluginMessage(Player player, String json) {
        System.out.println("[NetworkBalanceHandler] Sending plugin message to backend for player " + player.getUsername() +
                " on server " + player.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("none"));
        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        System.out.println(json);
        System.out.println(CHANNEL);

        player.getCurrentServer().ifPresent(serverConnection -> {
            serverConnection.sendPluginMessage(CHANNEL, data);
        });
    }
}
