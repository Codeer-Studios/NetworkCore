package networkcore;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "networkcore", name = "NetworkCore", version = "1.0", authors = {"Thomas Froud"})
public class NetworkCore {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public NetworkCore(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("[NetworkCore] Proxy initialized");

        // Register plugin message listener
        server.getEventManager().register(this, new VelocityPluginMessageListener(server));
    }

}
