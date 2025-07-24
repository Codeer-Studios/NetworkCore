package NetworkCore.Configs;

public class CoreConfig {
    public Database database;

    public static class Database {
        public String host;
        public int port;
        public String name;
        public String user;
        public String password;
        public int poolSize;
    }
}
