package de.cursedbreath.bansystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.commands.CMD_ban;
import de.cursedbreath.bansystem.commands.CMD_history;
import de.cursedbreath.bansystem.commands.CMD_unban;
import de.cursedbreath.bansystem.listener.ConnectionListener;
import de.cursedbreath.bansystem.utils.UpdateChecker;
import de.cursedbreath.bansystem.utils.configuration.VelocityConfig;
import de.cursedbreath.bansystem.utils.mysql.MySQLConnectionPool;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;

@Plugin(
        id = "bansystem",
        name = "Bansystem",
        version = "1.0.6",
        description = "A Simple ID Ban System",
        authors = {"Cursedbreath"}
)
public class BanSystem {

    @Inject
    private final Logger logger;

    private final ProxyServer proxyServer;

    private static VelocityConfig velocityConfig;

    private static MySQLConnectionPool mySQLConnectionPool;

    private CommandManager commandManager;

    private EventManager eventManager;

    private static BanSystem instance;

    @Inject
    public BanSystem(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.instance = this;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when the plugin is enabled.
     * @param event
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("§c  ____                    ____                  _                      ");
        logger.info("§c | __ )    __ _   _ __   / ___|   _   _   ___  | |_    ___   _ __ ___  ");
        logger.info("§c |  _ \\   / _` | | '_ \\  \\___ \\  | | | | / __| | __|  / _ \\ | '_ ` _ \\ ");
        logger.info("§c | |_) | | (_| | | | | |  ___) | | |_| | \\__ \\ | |_  |  __/ | | | | | |");
        logger.info("§c |____/   \\__,_| |_| |_| |____/   \\__, | |___/  \\__|  \\___| |_| |_| |_|");
        logger.info("§c                                  |___/                           §7v" + getVersion());

        velocityConfig = new VelocityConfig(logger, proxyServer);

        velocityConfig.createConfigs();

        try {
            velocityConfig.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MySQLStandardFunctions.executeScript(this.getClass().getResourceAsStream("/schema.sql"));

        eventManager = proxyServer.getEventManager();
        commandManager = proxyServer.getCommandManager();

        registerCommands();
        registerEvents();

        try {
            new UpdateChecker("czeStruK", logger);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Current Instance of the Plugin.
     * @return BanSystem
     */
    public static BanSystem getInstance() {
        return instance;
    }

    /**
     * Returns the Current Version of the Plugin.
     * @return String
     */
    public static String getVersion() {
        return "1.0.6";
    }

    /**
     * Returns the current MySQLConnectionPool.
     * @return MySQLConnectionPool
     */
    public static MySQLConnectionPool getMySQLConnectionPool() {
        return mySQLConnectionPool;
    }

    /**
     * Returns the current VelocityConfig.
     * @return VelocityConfig
     */
    public static VelocityConfig getVelocityConfig() {
        return velocityConfig;
    }

    /**
     * Sets a new MySQLConnectionPool.
     * @param mySQLConnectionPool
     */
    public static void setMySQLConnectionPool(MySQLConnectionPool mySQLConnectionPool) {
        BanSystem.mySQLConnectionPool = mySQLConnectionPool;
    }

    /**
     * Registers all Commands.
     */
    private void registerCommands() {
        commandManager.register(commandManager.metaBuilder("netban").build(), new CMD_ban(proxyServer, logger));
        commandManager.register(commandManager.metaBuilder("netunban").build(), new CMD_unban(proxyServer, logger));
        commandManager.register(commandManager.metaBuilder("nethistory").build(), new CMD_history(proxyServer));
    }

    /**
     * Registers all Events.
     */
    private void registerEvents() {
        eventManager.register(this, new ConnectionListener(proxyServer, logger));
    }

}
