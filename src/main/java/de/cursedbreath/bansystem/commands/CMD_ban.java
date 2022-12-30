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

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
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
            if(invocation.arguments().length != 2) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /ban <player> <reasonid>", NamedTextColor.RED));
                return;
            }

            String playername = invocation.arguments()[0];
            String reasonid = invocation.arguments()[1];

            if(proxyServer.getPlayer(playername).isEmpty()) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("playernotfound")));
                return;
            }

            if(!BanSystem.getVelocityConfig().isID(reasonid)) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("idnotfound")));
                return;
            }

            if(!invocation.source().hasPermission("bansystem."+reasonid)){
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("notenounghpermissions")));
                return;
            }

            if(invocation.source() instanceof Player player) {

                Player target = proxyServer.getPlayer(playername).get();

                if(target.hasPermission("bansystem.bypass")) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass")
                            .replaceAll("%player%", playername)));
                    return;
                }

                String reason = BanSystem.getVelocityConfig().getReason(reasonid);
                long time;
                if(BanSystem.getVelocityConfig().getDuration(reasonid) == 0) {
                    time = 0;
                }
                else
                {
                    time = System.currentTimeMillis() + BanSystem.getVelocityConfig().getDuration(reasonid);
                }
                String bannedby = player.getUsername();

                try {
                    MySQLStandardFunctions.insertBAN(target.getUniqueId().toString(), target.getUsername(), reason, bannedby, time);
                    MySQLStandardFunctions.insertHistory(target.getUniqueId().toString(), target.getUsername(), reason, bannedby, time);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                        .replaceAll("%player%", playername)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%reason%", reason)
                        .replaceAll("%time%", GlobalVariables.convertTime(time)));

                target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                        .replaceAll("%reason%", reason)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%time%", GlobalVariables.convertTime(time))));
            }
            else
            {

                Player target = proxyServer.getPlayer(playername).get();

                if(target.hasPermission("bansystem.bypass")) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cYou can't ban this player!", NamedTextColor.RED));
                    return;
                }

                String reason = BanSystem.getVelocityConfig().getReason(reasonid);
                long time;
                if(BanSystem.getVelocityConfig().getDuration(reasonid) == 0) {
                    time = 0;
                }
                else
                {
                    time = System.currentTimeMillis() + BanSystem.getVelocityConfig().getDuration(reasonid);
                }
                String bannedby = "CONSOLE";

                try {
                    MySQLStandardFunctions.insertBAN(target.getUniqueId().toString(), target.getUsername(), reason, bannedby, time);
                    MySQLStandardFunctions.insertHistory(target.getUniqueId().toString(), target.getUsername(), reason, bannedby, time);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                        .replaceAll("%player%", playername)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%reason%", reason)
                        .replaceAll("%time%", GlobalVariables.convertTime(time)));

                target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                        .replaceAll("%reason%", reason)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%time%", GlobalVariables.convertTime(time))));
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
}
