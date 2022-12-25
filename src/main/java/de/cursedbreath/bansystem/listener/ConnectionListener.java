package de.cursedbreath.bansystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionListener {

    private final ProxyServer proxyServer;

    private final Logger logger;

    /**
     * Constructor for ConnectionListener
     * @param proxyServer
     * @param logger
     */
    public ConnectionListener(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    /**
     * Event for PostLoginEvent
     * @param event
     */
    @Subscribe
    public void onPlayerConnect(PostLoginEvent event) {
        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), () -> {
            Player player = event.getPlayer();
            String uuid = player.getUniqueId().toString();
            String name = player.getUsername();

            try {
                if(MySQLStandardFunctions.playerExists(uuid)) {
                    if(Objects.equals(MySQLStandardFunctions.getNAME(uuid), name)) {
                        if(MySQLStandardFunctions.checkBAN(uuid)) {
                            ResultSet resultSet = MySQLStandardFunctions.getBAN(uuid);
                            String reason = resultSet.getString("reason");
                            String bannedby = resultSet.getString("bannedby");
                            long time = resultSet.getLong("banneduntil");
                            if(event.getPlayer().hasPermission("bansystem.bypass")) {
                                return;
                            }
                            if(time == 0) {
                                String Message = "§cYou are banned from this Server!\n\n§7Reason: §c" + reason + "\n§7Banned by: §c" + bannedby + "\n§7Banned until: §c" + "PERMANENT";

                                player.disconnect(Component.text(Message));
                            }
                            if(System.currentTimeMillis() < time) {
                                String Message = "§cYou are banned from this Server!\n\n§7Reason: §c" + reason + "\n§7Banned by: §c" + bannedby + "\n§7Banned until: §c" + GlobalVariables.convertTime(time);

                                player.disconnect(Component.text(Message));
                            }
                            else
                            {
                                MySQLStandardFunctions.deleteBAN(uuid);
                            }
                        }
                    } else {
                        MySQLStandardFunctions.updatePLAYERNAME(name, uuid);
                        if(MySQLStandardFunctions.checkBAN(uuid)) {
                            ResultSet resultSet = MySQLStandardFunctions.getBAN(uuid);
                            String reason = resultSet.getString("reason");
                            String bannedby = resultSet.getString("bannedby");
                            long time = resultSet.getLong("banneduntil");
                            if(event.getPlayer().hasPermission("bansystem.bypass")) {
                                return;
                            }
                            if(System.currentTimeMillis() < time) {
                                String Message = "§cYou are banned from this Server!\n\n§7Reason: §c" + reason + "\n§7Banned by: §c" + bannedby + "\n§7Banned until: §c" + GlobalVariables.convertTime(time);

                                player.disconnect(Component.text(Message));
                            }
                            else
                            {
                                MySQLStandardFunctions.deleteBAN(uuid);
                            }
                        }
                    }
                } else {
                    MySQLStandardFunctions.insertPLAYER(uuid, name);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).schedule();
    }
}
