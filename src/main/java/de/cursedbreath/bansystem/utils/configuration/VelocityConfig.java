package de.cursedbreath.bansystem.utils.configuration;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.mysql.MySQLConnectionPool;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class VelocityConfig {

    private final Logger logger;

    private final ProxyServer proxyServer;

    static File configPath = new File("plugins/bansystem/");
    static File banidfile = new File("plugins/bansystem/banids.toml");

    static File messagefile = new File("plugins/bansystem/messages.toml");

    static File configfile = new File("plugins/bansystem/config.toml");


    public VelocityConfig(Logger logger, ProxyServer proxyServer) {
        this.logger = logger;
        this.proxyServer = proxyServer;
    }

    public void createConfigs() {
        if(!configPath.exists()) {
            configPath.mkdirs();
        }

        if(!configfile.exists()) {
            InputStream stream = BanSystem.getInstance().getClass().getResourceAsStream("/config.toml");
            try {
                Files.copy(stream, configfile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!banidfile.exists()) {
            InputStream stream = BanSystem.getInstance().getClass().getResourceAsStream("/banids.toml");
            try {
                Files.copy(stream, banidfile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!messagefile.exists()) {
            InputStream stream = BanSystem.getInstance().getClass().getResourceAsStream("/messages.toml");
            try {
                Files.copy(stream, messagefile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadConfig() throws IOException {
        Toml configread = new Toml().read(configfile);

         String host = configread.getString("MySQL.host");
         String port = configread.getString("MySQL.port");
         String database = configread.getString("MySQL.database");
         String username = configread.getString("MySQL.user");
         String password = configread.getString("MySQL.pass");

         BanSystem.setMySQLConnectionPool(new MySQLConnectionPool("jdbc:mariadb:://"+host+":"+port+"/"+database+"", username, password, 40000, logger));
    }

    public boolean isID(String id) {
        Toml reader = new Toml().read(banidfile);
        return reader.containsTable(id);
    }

    public String getReason(String id) {
        Toml reader = new Toml().read(banidfile);
        String key = id + ".reason";
        return reader.getString(key);
    }

    public List<Long> getDurations(String id) {
        Toml reader = new Toml().read(banidfile);
        Toml ids = reader.getTable(id);
        List<Long> values = ids.getList("durations");
        return values;
    }

    public String getMessage(String key) {
        Toml reader = new Toml().read(messagefile);
        return reader.getString("Messages."+key).replaceAll("&", "§");
    }

}
