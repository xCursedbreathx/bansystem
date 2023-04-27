package de.cursedbreath.bansystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLFunctions;
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
                if(!MySQLFunctions.isPlayerInDatabase(uuid)) {

                    if(player.hasPermission("bansystem.bypass")) {

                        MySQLFunctions.createNewPlayer(uuid, name, true);
                        return;

                    }

                    MySQLFunctions.createNewPlayer(uuid, name, false);
                    return;

                }

                if(!MySQLFunctions.checkPlayerName(uuid, name)) {

                    MySQLFunctions.updatePlayerName(uuid, name);

                }

                if(player.hasPermission("bansystem.bypass")) {

                    if(!MySQLFunctions.isProtected(uuid)) {

                        MySQLFunctions.updateProtectedField(uuid, true);

                    }

                    return;

                }
                else
                {

                    if(MySQLFunctions.isProtected(uuid)) {

                        MySQLFunctions.updateProtectedField(uuid, false);

                    }

                }

                if(!MySQLFunctions.isGlobalBanned(uuid)) {
                    return;
                }

                ResultSet banData = MySQLFunctions.requestGlobalBanData(uuid);

                if(banData == null) {

                    BanSystem.getVelocityConfig().notifyADMINS(GlobalVariables.PREFIX + "§cActive Global Ban but could not get ban data for player " + name + " (" + uuid + ")");

                    BanSystem.getVelocityConfig().notifyADMINS(GlobalVariables.PREFIX + "§cPlease check Console for any errors and report the issue.");

                    return;

                }

                String reason = banData.getString("reason");
                String bannedby = banData.getString("banby");
                long time = banData.getLong("banuntil");

                if(time == 0) {

                    player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                            .replaceAll("%reason%", reason)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%time%", "PERMANENT")));

                    return;

                }

                if(time <= System.currentTimeMillis()) {

                    MySQLFunctions.deleteGlobalBan(uuid);

                    return;

                }

                player.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                        .replaceAll("%reason%", reason)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%time%", GlobalVariables.convertTime(time))));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).schedule();
    }

    @Subscribe
    public void onServerSwitch(ServerPreConnectEvent event) {

        Player player = event.getPlayer();

        String servername = event.getOriginalServer().getServerInfo().getName().toLowerCase();


        try {

            ResultSet banData = MySQLFunctions.requestServerBanData(player.getUniqueId());

            if(banData == null) {
                return;
            }

            String reason = banData.getString("sbanreason");
            String bannedby = banData.getString("sbanby");
            String bannedservername = banData.getString("servername").toLowerCase();
            long time = banData.getLong("sbanuntil");

            if(bannedservername.contains(BanSystem.getVelocityConfig().getLobbyName().toLowerCase())) {
                return;
            }

            if(bannedservername.contains("*")) {

                if(!servername.contains(bannedservername.replace("*", ""))) {
                    return;
                }

                if(time == 0) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    event.getPlayer().createConnectionRequest(event.getPreviousServer()).fireAndForget();
                    player.sendMessage(Component.text("§cYou are Permanently banned from this server on this Network!"));

                    return;
                }

                if(time > System.currentTimeMillis()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    event.getPlayer().createConnectionRequest(event.getPreviousServer()).fireAndForget();
                    player.sendMessage(Component.text("§cYou are banned from this Server on this Network!"));
                    player.sendMessage(Component.text("§cReason: " + reason));
                    player.sendMessage(Component.text("§cBanned by: " + bannedby));
                    player.sendMessage(Component.text("§cBanned until: " + GlobalVariables.convertTime(time)));
                }

                return;

            }

            if(!bannedservername.equalsIgnoreCase(servername)) {
                return;
            }

            if(time == 0) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                event.getPlayer().createConnectionRequest(event.getPreviousServer()).fireAndForget();
                player.sendMessage(Component.text("§cYou are Permanently banned from this server on this Network!"));

                return;
            }

            if(time > System.currentTimeMillis()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                event.getPlayer().createConnectionRequest(event.getPreviousServer()).fireAndForget();
                player.sendMessage(Component.text("§cYou are banned from this Server on this Network!"));
                player.sendMessage(Component.text("§cReason: " + reason));
                player.sendMessage(Component.text("§cBanned by: " + bannedby));
                player.sendMessage(Component.text("§cBanned until: " + GlobalVariables.convertTime(time)));
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
