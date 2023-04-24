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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CMD_unban implements SimpleCommand {

    private final ProxyServer proxyServer;

    private final Logger logger;

    public CMD_unban(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), ()->{
            if(invocation.arguments().length < 2) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /netunban <player> <global/server> <servername>", NamedTextColor.RED));
                return;
            }

            String playername = invocation.arguments()[0];

            String type = invocation.arguments()[1];

            if(type.equalsIgnoreCase("server") && invocation.arguments().length != 3) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /netunban <player> <global/server> <servername>", NamedTextColor.RED));
                return;
            }

            UUID uuid = MySQLFunctions.getUUID(playername);


            /**
             * Unban Player
             */
            if(invocation.source() instanceof Player player) {

                String unbannedby = player.getUsername();


                if(uuid == null) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("playernotfound")));
                    return;
                }


                if(type.equalsIgnoreCase("server")) {

                    String servername = invocation.arguments()[2];

                    if(!MySQLFunctions.isServerBanned(uuid, servername)) {

                        invocation.source().sendMessage(Component.text("§cThis Player is not banned on Server: " + servername, NamedTextColor.RED));

                        return;

                    }

                    MySQLFunctions.deleteServerBan(uuid);

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                                    BanSystem.getVelocityConfig().getMessage("unbannotify")
                                            .replaceAll("%player%", playername)
                                            .replaceAll("%by%", unbannedby)

                    );

                }

                if(type.equalsIgnoreCase("global")) {

                    if(!MySQLFunctions.isGlobalBanned(uuid)) {

                        invocation.source().sendMessage(Component.text("§cThis Player is not globally banned!", NamedTextColor.RED));

                        return;

                    }

                    MySQLFunctions.deleteGlobalBan(uuid);

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                                    BanSystem.getVelocityConfig().getMessage("unbannotify")
                                            .replaceAll("%player%", playername)
                                            .replaceAll("%by%", unbannedby)

                    );

                }

            }
            else
            {
                String unbannedby = "CONSOLE";

                if(uuid == null) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("playernotfound")));
                    return;
                }


                if(type.equalsIgnoreCase("server")) {

                    String servername = invocation.arguments()[2];

                    if(!MySQLFunctions.isServerBanned(uuid, servername)) {

                        invocation.source().sendMessage(Component.text("§cThis Player is not banned on Server: " + servername, NamedTextColor.RED));

                        return;

                    }

                    MySQLFunctions.deleteServerBan(uuid);

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                                    BanSystem.getVelocityConfig().getMessage("unbannotify")
                                            .replaceAll("%player%", playername)
                                            .replaceAll("%by%", unbannedby)

                    );

                }

                if(type.equalsIgnoreCase("global")) {

                    if(!MySQLFunctions.isGlobalBanned(uuid)) {

                        invocation.source().sendMessage(Component.text("§cThis Player is not globally banned!", NamedTextColor.RED));

                        return;

                    }

                    MySQLFunctions.deleteGlobalBan(uuid);

                    BanSystem.getVelocityConfig().notifyADMINS(

                            GlobalVariables.PREFIX
                                    +
                            BanSystem.getVelocityConfig().getMessage("unbannotify")
                                    .replaceAll("%player%", playername)
                                    .replaceAll("%by%", unbannedby)

                    );

                }

            }
        }).schedule();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if(invocation.arguments().length == 1) {

            return proxyServer.getAllPlayers()
                    .stream()
                    .map(Player::getUsername)
                    .filter(s -> s.startsWith(invocation.arguments()[0]))
                    .collect(Collectors.toList());

        }

        if(invocation.arguments().length == 2) {

            return Arrays.asList("global", "server");

        }

        if(invocation.arguments().length == 3) {

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
        return invocation.source().hasPermission("bansystem.unban");
    }

    /**
     * Notify all Admins
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
}
