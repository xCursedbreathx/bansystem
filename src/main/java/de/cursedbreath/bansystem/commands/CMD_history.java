package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import de.cursedbreath.bansystem.utils.mysql.MySQLFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class CMD_history implements SimpleCommand {

    private final ProxyServer proxyServer;

    /**
     * History command constructor
     * @param proxyServer ProxyServer to run this async.
     */
    public CMD_history(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {

        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), ()->{

            if(invocation.arguments().length != 1) {

                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /nethistory <player>", NamedTextColor.RED));

                return;

            }

            String playername = invocation.arguments()[0];

            UUID uuid = MySQLFunctions.getUUID(playername);

            if(uuid == null) {

                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cPlayer not found!", NamedTextColor.RED));

                return;

            }
            if(invocation.source() instanceof Player player) {

                MySQLFunctions.getHistory(player, uuid.toString());

                MySQLFunctions.newCommandLog(player.getUsername(), playername, "history");

            }
            else
            {

                invocation.source().sendMessage(Component.text(BanSystem.getVelocityConfig().getMessage("onlyplayercommand")));

            }

        }).schedule();

    }

    @Override
    public boolean hasPermission(Invocation invocation) {

        return invocation.source().hasPermission("bansystem.history");

    }

}
