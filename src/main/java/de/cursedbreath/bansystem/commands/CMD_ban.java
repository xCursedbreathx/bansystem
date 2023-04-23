package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class CMD_ban implements SimpleCommand {

    private final ProxyServer proxyServer;

    private final Logger logger;

    public CMD_ban(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), ()->{

            if(!(invocation.arguments().length >= 3)) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /ban <player> <reasonid> <global/server> <servername>", NamedTextColor.RED));
            }

            String playername = invocation.arguments()[0];
            String reasonid = invocation.arguments()[1];
            String type = invocation.arguments()[2];

            if(type.equalsIgnoreCase("server") && invocation.arguments().length != 4) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /ban <player> <reasonid> <global/server> <servername>", NamedTextColor.RED));
                return;
            }

            String reason = BanSystem.getVelocityConfig().getReason(reasonid);

            long time = -1;

            if(invocation.source() instanceof Player sender) {

                String bannedby = sender.getUsername();

                if(!sender.hasPermission("bansystem." + reasonid)) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("notenounghpermissions"), NamedTextColor.RED));
                    return;
                }

                Player target = null;

                if(proxyServer.getPlayer(playername).isPresent()) {
                    target = proxyServer.getPlayer(playername).get();
                }

                String uuid = null;

                uuid = MySQLStandardFunctions.getUUIDFromName(playername);

                if(uuid == null) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("playernotfound"), NamedTextColor.RED));
                    return;
                }

                if(target != null) {
                    uuid = target.getUniqueId().toString();
                }

                if(target.hasPermission("bansystem.bypass")) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass"), NamedTextColor.RED));
                    return;
                }

                time = GlobalVariables.calculateBanTime(MySQLStandardFunctions.newGetBannedTimes(uuid, Integer.valueOf(reasonid)), reasonid);

                if(time == -1) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + "Could not calculate bantime please contact a admin.", NamedTextColor.RED));
                }

                if(MySQLStandardFunctions.isGlobalBanned(UUID.fromString(uuid))) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already global banned.", NamedTextColor.RED));
                    return;
                }

                if(type.equalsIgnoreCase("server")) {

                    String servername = invocation.arguments()[3];

                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cStart banning Player on Server " + servername + ".", NamedTextColor.RED));

                    if(!proxyServer.getAllServers().stream().anyMatch((data) -> data.getServerInfo().getName().equalsIgnoreCase(servername))) {
                        invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cThe Server " + servername + " does not exist. Please make sure you use the name from the velocity config.", NamedTextColor.RED));
                        return;
                    }

                    if(time == 0) {

                        //Permanently ban Player from Server

                        if(MySQLStandardFunctions.isServerBanned(UUID.fromString(uuid), servername)) {
                            sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already banned on a Server and will now be global banned.", NamedTextColor.RED));
                            MySQLStandardFunctions.newBan(uuid, reason, bannedby, "global", servername, 0, Integer.valueOf(reasonid));
                            return;
                        }

                        MySQLStandardFunctions.newBan(uuid, reason, bannedby, "server", servername, 0, Integer.valueOf(reasonid));

                        return;

                    }

                    time = System.currentTimeMillis() + time;

                    if(MySQLStandardFunctions.isServerBanned(UUID.fromString(uuid), servername)) {
                        sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already banned on a Server and will now be global banned.", NamedTextColor.RED));
                        MySQLStandardFunctions.removeBanFromDatabase(uuid);
                        MySQLStandardFunctions.newBan(uuid, reason, bannedby, "global", servername, time, Integer.valueOf(reasonid));

                        disconnectPlayer(target, reason, bannedby, time);

                        return;
                    }

                    MySQLStandardFunctions.newBan(uuid, reason, bannedby, "server", servername, time, Integer.valueOf(reasonid));

                    movePlayerToLobby(target);

                }

                if(type.equalsIgnoreCase("global")) {

                    //Permanently ban Player from Network.

                    if(MySQLStandardFunctions.isServerBanned(UUID.fromString(uuid), servername)) {
                        sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already banned on a Server and will now be global banned.", NamedTextColor.RED));
                        MySQLStandardFunctions.removeBanFromDatabase(uuid);
                        MySQLStandardFunctions.newBan(uuid, reason, bannedby, "global", null, time, Integer.valueOf(reasonid));

                        disconnectPlayer(target, reason, bannedby, time);

                        return;
                    }


                }

            }

        }).schedule();
    }

    /**
     * Suggests Online Players for the Tab-Completion
     * @param invocation the invocation context
     * @return
     */
    @Override
    public List<String> suggest(Invocation invocation) {
        if(invocation.arguments().length == 1) {
            return proxyServer.getAllPlayers().stream().map(Player::getUsername).filter(s -> s.startsWith(invocation.arguments()[0])).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.ban");
    }

    /**
     * Notifies all online admins about a ban
     * @param message
     */
    private void notifyADMINS(String message) {
        proxyServer.getAllPlayers()
                .stream()
                .filter(player ->
                        player.hasPermission("bansystem.notify"))
                .forEach(player ->
                        player.sendMessage(Component.text(message)));
    }

    private void disconnectPlayer(Player target, String reason, String bannedby, long time) {

        target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                .replaceAll("%reason%", reason)
                .replaceAll("%by%", bannedby)
                .replaceAll("%time%", GlobalVariables.convertTime(time))));

    }

    private void movePlayerToLobby(Player target) {
        proxyServer.getAllServers().stream().anyMatch((data) -> data.getServerInfo().getName().contains("lobby") || data.getServerInfo().getName().contains("Lobby"));
    }

}
