package de.cursedbreath.bansystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
            UUID uuid = player.getUniqueId();
            String name = player.getUsername();

            try {
                if(!MySQLStandardFunctions.isPlayerInDatabase(uuid)) {
                    MySQLStandardFunctions.createNewPlayer(uuid, name);
                    return;
                }

                if(!MySQLStandardFunctions.isGlobalBanned(uuid)) {
                    return;
                }

                ResultSet banData = MySQLStandardFunctions.getBanData(uuid);

                if(banData == null) {
                    return;
                }

                String reason = banData.getString("REASON");
                String bannedby = banData.getString("BANBY");
                String type = banData.getString("BANTYPE");
                long time = banData.getLong("BANNEDUNTIL");

                if(time == 0) {
                    player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                            .replaceAll("%reason%", reason)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%time%", "PERMANENT")));
                    return;
                }

                if(time >= System.currentTimeMillis()) {
                    MySQLStandardFunctions.removeBanFromDatabase(uuid);
                    return;
                }

                if(type.equalsIgnoreCase("global")) {
                    player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                            .replaceAll("%reason%", reason)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%time%", GlobalVariables.convertTime(time))));
                    return;
                }



//                if(MySQLStandardFunctions.playerExists(uuid)) {
//
//                    if(Objects.equals(MySQLStandardFunctions.getNAME(uuid), name)) {
//
//                        if(MySQLStandardFunctions.checkBAN(uuid)) {
//
//                            ResultSet resultSet = MySQLStandardFunctions.getBAN(uuid);
//                            String reason = resultSet.getString("reason");
//                            String bannedby = resultSet.getString("bannedby");
//                            long time = resultSet.getLong("banneduntil");
//                            if(event.getPlayer().hasPermission("bansystem.bypass")) {
//                                return;
//                            }
//                            if(time == 0) {
//
//                                player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
//                                        .replaceAll("%reason%", reason)
//                                        .replaceAll("%by%", bannedby)
//                                        .replaceAll("%time%", "PERMANENT")));
//
//                                return;
//                            }
//                            if(System.currentTimeMillis() < time) {
//
//                                player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
//                                        .replaceAll("%reason%", reason)
//                                        .replaceAll("%by%", bannedby)
//                                        .replaceAll("%time%", GlobalVariables.convertTime(time))));
//
//                            }
//                            else
//                            {
//                                MySQLStandardFunctions.deleteBAN(uuid);
//                            }
//                        }
//                    } else {
//
//                        MySQLStandardFunctions.updatePLAYERNAME(name, uuid);
//
//                        if(MySQLStandardFunctions.checkBAN(uuid)) {
//
//                            ResultSet resultSet = MySQLStandardFunctions.getBAN(uuid);
//                            String reason = resultSet.getString("reason");
//                            String bannedby = resultSet.getString("bannedby");
//                            long time = resultSet.getLong("banneduntil");
//
//                            if(event.getPlayer().hasPermission("bansystem.bypass")) {
//                                return;
//                            }
//
//                            if(time == 0) {
//
//                                player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
//                                        .replaceAll("%reason%", reason)
//                                        .replaceAll("%by%", bannedby)
//                                        .replaceAll("%time%", "PERMANENT")));
//
//                                return;
//                            }
//
//                            if(System.currentTimeMillis() < time) {
//
//                                player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
//                                        .replaceAll("%reason%", reason)
//                                        .replaceAll("%by%", bannedby)
//                                        .replaceAll("%time%", GlobalVariables.convertTime(time))));
//
//                            }
//                            else
//                            {
//                                MySQLStandardFunctions.deleteBAN(uuid);
//                            }
//                        }
//                    }
//                } else {
//                    MySQLStandardFunctions.insertPLAYER(uuid, name);
//                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).schedule();
    }

    @Subscribe
    public void onServerSwitch(ServerPreConnectEvent event) {

        Player player = event.getPlayer();

        String servername = event.getOriginalServer().getServerInfo().getName();


        try {

            ResultSet banData = MySQLStandardFunctions.getBanData(player.getUniqueId().toString());

            if(banData == null) {
                return;
            }

            String reason = banData.getString("REASON");
            String bannedby = banData.getString("BANBY");
            String bannedservername = banData.getString("BANSERVER");
            long time = banData.getLong("BANNEDUNTIL");

            if(!bannedservername.equalsIgnoreCase(servername)) {
                return;
            }

            if(time == 0) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.sendMessage(Component.text("§cYou are Permanent banned from this server on this Network!"));
            }

            if(time > System.currentTimeMillis()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.sendMessage(Component.text("§cYou are banned from this server on this Network!"));
                player.sendMessage(Component.text("§cReason: " + reason));
                player.sendMessage(Component.text("§cBanned by: " + bannedby));
                player.sendMessage(Component.text("§cBanned until: " + GlobalVariables.convertTime(time)));
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
