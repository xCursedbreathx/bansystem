package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.Arrays;
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

            if(!BanSystem.getVelocityConfig().isID(reasonid)) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("idnotfound"), NamedTextColor.RED));
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

                uuid = MySQLFunctions.getUUID(playername).toString();

                if(uuid == null) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("playernotfound"), NamedTextColor.RED));
                    return;
                }

                if(target != null) {
                    uuid = target.getUniqueId().toString();

                    if(target.hasPermission("bansystem.bypass")) {
                        sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass"), NamedTextColor.RED));
                        return;
                    }

                    movePlayerToLobby(target);

                }

                if(MySQLFunctions.isProtected(UUID.fromString(uuid))) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass"), NamedTextColor.RED));
                    return;
                }

                time = GlobalVariables.calculateBanTime(MySQLFunctions.getBannedTimesForID(uuid, reasonid), reasonid);

                if(time == -1) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + "Could not calculate bantime please contact a admin.", NamedTextColor.RED));
                }

                if(MySQLFunctions.isGlobalBanned(UUID.fromString(uuid))) {
                    sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already global banned.", NamedTextColor.RED));
                    return;
                }

                if(type.equalsIgnoreCase("server")) {

                    String servername = invocation.arguments()[3];

                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cStart banning Player on Server " + servername + ".", NamedTextColor.RED));

                    if(!proxyServer.getAllServers().stream().anyMatch((data) -> data.getServerInfo().getName().equalsIgnoreCase(servername)) && !proxyServer.getAllServers().stream().anyMatch((data) -> data.getServerInfo().getName().contains(servername.replace("*", "")))) {
                        invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cThe Server " + servername + " does not exist. Please make sure you use the name from the velocity config.", NamedTextColor.RED));
                        return;
                    }

                    if(time == 0) {

                        //Permanently ban Player from Server

                        if(MySQLFunctions.isServerBanned(UUID.fromString(uuid), servername)) {

                            sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already banned on this Server", NamedTextColor.RED));

                            return;

                        }

                        MySQLFunctions.newServerBan(uuid, reason, bannedby, servername, 0, Integer.valueOf(reasonid));

                        BanSystem.getVelocityConfig().notifyADMINS(

                                GlobalVariables.PREFIX
                                        +
                                BanSystem.getVelocityConfig().getMessage("bannotify")
                                        .replaceAll("%player%", playername)
                                        .replaceAll("%reason%", reason)
                                        .replaceAll("%by%", bannedby)
                                        .replaceAll("%time%", GlobalVariables.convertTime(time))
                                        .replaceAll("%bannedby%", bannedby)
                                        .replaceAll("%type%", "global")

                        );

                        return;

                    }

                    time = System.currentTimeMillis() + time;

                    if(MySQLFunctions.isServerBanned(UUID.fromString(uuid), servername)) {

                        sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already banned on this Server", NamedTextColor.RED));

                        return;

                    }

                    MySQLFunctions.newServerBan(uuid, reason, bannedby, servername, time, Integer.valueOf(reasonid));

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                                    BanSystem.getVelocityConfig().getMessage("bannotify")
                                            .replaceAll("%player%", playername)
                                            .replaceAll("%reason%", reason)
                                            .replaceAll("%by%", bannedby)
                                            .replaceAll("%time%", GlobalVariables.convertTime(time))
                                            .replaceAll("%bannedby%", bannedby)
                                            .replaceAll("%type%", "global")

                    );

                    return;

                }

                if(type.equalsIgnoreCase("global")) {

                    //Permanently ban Player from Network.

                    if(MySQLFunctions.isGlobalBanned(UUID.fromString(uuid))) {
                        sender.sendMessage(Component.text(GlobalVariables.PREFIX + "The Player " + playername + " is already global banned. Trying to Disconnect the Player now.", NamedTextColor.RED));

                        disconnectPlayer(target, reason, bannedby, time);

                        return;
                    }

                    if(time == 0) {

                        MySQLFunctions.newGlobalBan(uuid, reason, bannedby, 0, Integer.valueOf(reasonid));

                        BanSystem.getVelocityConfig().notifyADMINS(

                                GlobalVariables.PREFIX
                                        +
                                        BanSystem.getVelocityConfig().getMessage("bannotify")
                                                .replaceAll("%player%", playername)
                                                .replaceAll("%reason%", reason)
                                                .replaceAll("%by%", bannedby)
                                                .replaceAll("%time%", GlobalVariables.convertTime(time))
                                                .replaceAll("%bannedby%", bannedby)
                                                .replaceAll("%type%", "global")

                        );

                        return;

                    }

                    time = System.currentTimeMillis() + time;

                    MySQLFunctions.newGlobalBan(uuid, reason, bannedby, time, Integer.valueOf(reasonid));

                    disconnectPlayer(target, reason, bannedby, time);

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                                    BanSystem.getVelocityConfig().getMessage("bannotify")
                                            .replaceAll("%player%", playername)
                                            .replaceAll("%reason%", reason)
                                            .replaceAll("%by%", bannedby)
                                            .replaceAll("%time%", GlobalVariables.convertTime(time))
                                            .replaceAll("%bannedby%", bannedby)
                                            .replaceAll("%type%", "global")

                    );


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

            return proxyServer.getAllPlayers()
                    .stream()
                    .map(Player::getUsername)
                    .filter(s -> s.startsWith(invocation.arguments()[0]))
                    .collect(Collectors.toList());

        }

        if(invocation.arguments().length == 3) {

            return Arrays.asList("global", "server");

        }

        if(invocation.arguments().length == 4) {

            return proxyServer.getAllServers().stream()
                    .map(RegisteredServer::getServerInfo)
                    .map(ServerInfo::getName)
                    .filter(s -> s.startsWith(invocation.arguments()[3]))
                    .collect(Collectors.toList());

        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.ban");
    }

    private void disconnectPlayer(Player target, String reason, String bannedby, long time) {

        target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                .replaceAll("%reason%", reason)
                .replaceAll("%by%", bannedby)
                .replaceAll("%time%", GlobalVariables.convertTime(time))));

    }

    private void movePlayerToLobby(Player target) {

        proxyServer.getAllServers()
                .stream()
                .map(RegisteredServer::getServerInfo)
                .map(ServerInfo::getName)
                .filter(s -> s.contains("lobby"))
                .findFirst()
                .ifPresent(s -> target.createConnectionRequest(proxyServer.getServer(s).get())
                        .fireAndForget());

    }

}
