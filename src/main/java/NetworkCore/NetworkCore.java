package NetworkCore;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "NetworkCore", name = "NetworkCore", version = "1.0", authors = {"Thomas Froud"})
public final class NetworkCore {

    public static NetworkCore INSTANCE;
    public final ProxyServer server;
    public final Logger logger;
    public HikariDataSource dataSource;
    public final Gson gson = new Gson();

    @Inject
    public NetworkCore(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        INSTANCE = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Initializing NetworkCore...");
        setupDatabase();
        PluginMessageRouter.init();
    }

    private void setupDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/network?useSSL=false");
        config.setUsername("youruser");
        config.setPassword("yourpassword");
        config.setMaximumPoolSize(10);
        config.setPoolName("NetworkCore-Pool");

        dataSource = new HikariDataSource(config);
        logger.info("MySQL connection pool initialized.");
    }

    public static HikariDataSource getDataSource() {
        return INSTANCE.dataSource;
    }

    public static Gson getGson() {
        return INSTANCE.gson;
    }

    public static ProxyServer getProxy() {
        return INSTANCE.server;
    }

    public static Logger getLogger() {
        return INSTANCE.logger;
    }


}
