package NetworkCore;

import NetworkCore.Configs.CoreConfig;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.inject.Inject;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;


@Plugin(id = "NetworkCore", name = "NetworkCore", version = "1.0", authors = {"Thomas Froud"})
public final class NetworkCore {

    public static NetworkCore INSTANCE;
    public final ProxyServer server;
    public final Logger logger;
    public HikariDataSource dataSource;
    public final Gson gson = new Gson();
    public CoreConfig config;

    @Inject
    public NetworkCore(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        INSTANCE = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Initializing NetworkCore...");
        config = loadConfig();
        setupDatabase();
        PluginMessageRouter.init();
    }

    private void setupDatabase() {
        CoreConfig.Database db = config.database;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + db.host + ":" + db.port + "/" + db.name + "?useSSL=false");
        config.setUsername(db.user);
        config.setPassword(db.password);
        config.setMaximumPoolSize(db.poolSize);
        config.setPoolName("NetworkCore-Pool");

        dataSource = new HikariDataSource(config);
        logger.info("MySQL connection pool initialized.");
    }

    private CoreConfig loadConfig() {
        try {
            File dataFolder = new File("plugins/NetworkCore");
            if (!dataFolder.exists()) dataFolder.mkdirs();

            File configFile = new File(dataFolder, "config.yml");
            if (!configFile.exists()) {
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    Files.copy(in, configFile.toPath());
                    logger.info("Generated default config.yml");
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(configFile.toPath())) {
                return yaml.loadAs(in, CoreConfig.class);
            }

        } catch (Exception e) {
            logger.severe("Failed to load config.yml: " + e.getMessage());
            throw new RuntimeException(e);
        }
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
