package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.UUID;

public class CMD_unban implements SimpleCommand {

    private final ProxyServer proxyServer;

    private final Logger logger;

    public CMD_unban(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        if(invocation.arguments().length != 1) {
            invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /unban <player>", NamedTextColor.RED));
            return;
        }
        String playername = invocation.arguments()[0];

        /**
         * Unban Player
         */
        if(invocation.source() instanceof Player player) {
            String unbannedby = player.getUsername();
            UUID uuid = MySQLStandardFunctions.getUUID(playername);
            /**
             * Check if Player is Banned.
             */
            if(MySQLStandardFunctions.checkBAN(uuid.toString())) {
                MySQLStandardFunctions.deleteBAN(uuid.toString());
                notifyADMINS(GlobalVariables.PREFIX + "User " + playername + " was unbanned by " + unbannedby);
            }
        }
        else
        {
            String unbannedby = "CONSOLE";
            UUID uuid = MySQLStandardFunctions.getUUID(playername);
            if(MySQLStandardFunctions.checkBAN(uuid.toString())) {
                MySQLStandardFunctions.deleteBAN(uuid.toString());
                notifyADMINS(GlobalVariables.PREFIX + "User " + playername + " was unbanned by " + unbannedby);
            }
        }
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
