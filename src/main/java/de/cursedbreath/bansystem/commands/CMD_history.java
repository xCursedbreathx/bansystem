package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLStandardFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.SQLException;
import java.util.UUID;

public class CMD_history implements SimpleCommand {

    private final ProxyServer proxyServer;

    public CMD_history(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {
        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), ()->{
            if(invocation.arguments().length != 1) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /history <player>", NamedTextColor.RED));
                return;
            }
            String playername = invocation.arguments()[0];
            try {
                UUID uuid = MySQLStandardFunctions.getUUID(playername);
                if(uuid == null) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cPlayer not found!", NamedTextColor.RED));
                    return;
                }
                if(invocation.source() instanceof Player player) {
                    MySQLStandardFunctions.getHistory(player, uuid.toString());
                }
                else
                {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§7History is Currently only visible by Players!", NamedTextColor.RED));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).schedule();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.history");
    }
}
