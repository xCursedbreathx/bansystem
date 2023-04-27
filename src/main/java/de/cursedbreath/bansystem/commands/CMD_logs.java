package de.cursedbreath.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.mysql.MySQLFunctions;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CMD_logs implements SimpleCommand {

    private final ProxyServer proxyServer;

    /**
     * Logs command constructor
     * @param proxyServer ProxyServer to run this async.
     */
    public CMD_logs(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {

        proxyServer.getScheduler().buildTask(BanSystem.getInstance(), () -> {

            if(invocation.source() instanceof Player player) {

                if(invocation.arguments().length > 1) {

                    player.sendMessage(Component.text("§cUsage: /logs <limit>"));

                }

                int limit = 10;

                if(invocation.arguments().length == 1) {

                    try {

                        limit = Integer.parseInt(invocation.arguments()[0]);

                    } catch (NumberFormatException e) {

                        player.sendMessage(Component.text("§cUsage: /logs <limit>"));

                    }

                }

                MySQLFunctions.getLogs(player, limit);

            }
            else
            {

                invocation.source().sendMessage(Component.text(BanSystem.getVelocityConfig().getMessage("onlyplayercommand")));

            }

        }).schedule();

    }

    @Override
    public List<String> suggest(Invocation invocation) {

        if(invocation.arguments().length == 1) {

            return Arrays.asList("10", "25", "50", "100");

        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bansystem.commandlogs");
    }
}
