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
            if(invocation.arguments().length >= 3) {
                invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cUsage: /netban <player> <reasonid> <global/server> <Servername>", NamedTextColor.RED));
                return;
            }

            String playername = invocation.arguments()[0];
            String reasonid = invocation.arguments()[1];
            String type = invocation.arguments()[2];
            String servername;

            try {
                servername = invocation.arguments()[3];
            } catch (Exception e) {
                servername = "null";
            }

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

            String reason = BanSystem.getVelocityConfig().getReason(reasonid);

            if(invocation.source() instanceof Player sender) {



                // Start of Player Banning a Player



                Player target = proxyServer.getPlayer(playername).get();

                if(target.hasPermission("bansystem.bypass")) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass")
                            .replaceAll("%player%", playername)));
                    return;
                }

                long time;

                String bannedby = sender.getUsername();

                int BannedTimes = -1;

                BannedTimes = MySQLStandardFunctions.newgetBannedTimes(target.getUniqueId().toString(), Integer.valueOf(reasonid));

                if(BannedTimes == -1){
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cAn error occurred while trying to fetch the BannedTimes from this player!", NamedTextColor.RED));
                    return;
                }

                time = GlobalVariables.calculateBanTime(BannedTimes, reasonid);

                if(time == 0) {
                    notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                            .replaceAll("%player%", playername)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%reason%", reason)
                            .replaceAll("%time%", "PERMANENT"));

                    target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                            .replaceAll("%reason%", reason)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%time%", "PERMANENT")));

                    if(!type.equalsIgnoreCase("global")) {
                        if(MySQLStandardFunctions.isServerBanned(target.getUniqueId().toString())) {
                            MySQLStandardFunctions.removeBanFromDatabase(target.getUniqueId().toString());
                            MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, "global", servername, time, Integer.valueOf(reasonid));
                            return;
                        }
                    }

                    if(type.equalsIgnoreCase("global")) {
                        if(MySQLStandardFunctions.isGlobalBanned(target.getUniqueId().toString())) {
                            invocation.source().sendMessage(Component.text("This Player is already Global Banned.", NamedTextColor.RED));
                            return;
                        }
                    }
                    MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, type, servername, time, Integer.valueOf(reasonid));


                    return;
                }
                else
                {
                    time = System.currentTimeMillis() + time;
                }

                if(!type.equalsIgnoreCase("global")) {
                    if(MySQLStandardFunctions.isServerBanned(target.getUniqueId().toString())) {
                        MySQLStandardFunctions.removeBanFromDatabase(target.getUniqueId().toString());
                        MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, "global", servername, time, Integer.valueOf(reasonid));
                        return;
                    }
                }

                if(type.equalsIgnoreCase("global")) {
                    if(MySQLStandardFunctions.isGlobalBanned(target.getUniqueId().toString())) {
                        invocation.source().sendMessage(Component.text("This Player is already Global Banned.", NamedTextColor.RED));
                        return;
                    }
                }
                MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, type, servername, time, Integer.valueOf(reasonid));

                notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                        .replaceAll("%player%", playername)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%reason%", reason)
                        .replaceAll("%time%", GlobalVariables.convertTime(time)));

                target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                        .replaceAll("%reason%", reason)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%time%", GlobalVariables.convertTime(time))));



                // End of Player Banning a Player



            }
            else
            {



                // Start of Console Banning a Player



                Player target = proxyServer.getPlayer(playername).get();

                if(target.hasPermission("bansystem.bypass")) {
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("banbypass")
                            .replaceAll("%player%", playername)));
                    return;
                }

                long time;

                String bannedby = "CONSOLE";

                int BannedTimes = -1;

                BannedTimes = MySQLStandardFunctions.newgetBannedTimes(target.getUniqueId().toString(), Integer.valueOf(reasonid));

                if(BannedTimes == -1){
                    invocation.source().sendMessage(Component.text(GlobalVariables.PREFIX + "§cAn error occurred while trying to fetch the BannedTimes from this player!", NamedTextColor.RED));
                    return;
                }

                time = GlobalVariables.calculateBanTime(BannedTimes, reasonid);

                if(time == 0) {
                    notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                            .replaceAll("%player%", playername)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%reason%", reason)
                            .replaceAll("%time%", "PERMANENT"));

                    target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                            .replaceAll("%reason%", reason)
                            .replaceAll("%by%", bannedby)
                            .replaceAll("%time%", "PERMANENT")));

                    if(!type.equalsIgnoreCase("global")) {
                        if(MySQLStandardFunctions.isServerBanned(target.getUniqueId().toString())) {
                            MySQLStandardFunctions.removeBanFromDatabase(target.getUniqueId().toString());
                            MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, "global", servername, time, Integer.valueOf(reasonid));
                            return;
                        }
                    }

                    if(type.equalsIgnoreCase("global")) {
                        if(MySQLStandardFunctions.isGlobalBanned(target.getUniqueId().toString())) {
                            invocation.source().sendMessage(Component.text("This Player is already Global Banned.", NamedTextColor.RED));
                            return;
                        }
                    }
                    MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, type, servername, time, Integer.valueOf(reasonid));


                    return;
                }
                else
                {
                    time = System.currentTimeMillis() + time;
                }

                if(!type.equalsIgnoreCase("global")) {
                    if(MySQLStandardFunctions.isServerBanned(target.getUniqueId().toString())) {
                        MySQLStandardFunctions.removeBanFromDatabase(target.getUniqueId().toString());
                        MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, "global", servername, time, Integer.valueOf(reasonid));
                        return;
                    }
                }

                if(type.equalsIgnoreCase("global")) {
                    if(MySQLStandardFunctions.isGlobalBanned(target.getUniqueId().toString())) {
                        invocation.source().sendMessage(Component.text("This Player is already Global Banned.", NamedTextColor.RED));
                        return;
                    }
                }
                MySQLStandardFunctions.newBan(target.getUniqueId().toString(), reason, bannedby, type, servername, time, Integer.valueOf(reasonid));

                notifyADMINS(GlobalVariables.PREFIX + BanSystem.getVelocityConfig().getMessage("bannotify")
                        .replaceAll("%player%", playername)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%reason%", reason)
                        .replaceAll("%time%", GlobalVariables.convertTime(time)));

                target.disconnect(Component.text(BanSystem.getVelocityConfig().getMessage("bannedscreen")
                        .replaceAll("%reason%", reason)
                        .replaceAll("%by%", bannedby)
                        .replaceAll("%time%", GlobalVariables.convertTime(time))));



                // End of Console Banning a Player



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
