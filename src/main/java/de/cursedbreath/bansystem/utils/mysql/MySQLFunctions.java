package de.cursedbreath.bansystem.utils.mysql;

import com.velocitypowered.api.proxy.Player;
import de.cursedbreath.bansystem.BanSystem;
import net.kyori.adventure.text.Component;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class MySQLFunctions {

    //Script Executor

    /**
     * Executes a SQL Script from a File
     * @param inputStream
     */
    public static void executeScript(InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("InputStream for script is null!");
        }

        Connection conn;
        Statement statement = null;

        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try (Scanner scanner = new Scanner(inputStream)) {
                scanner.useDelimiter("/\\*[\\s\\S]*?\\*/|--[^\\r\\n]*|;");
                statement = conn.createStatement();

                while (scanner.hasNext()) {
                    String line = scanner.next().trim();

                    if (!line.isEmpty()) {
                        statement.execute(line);
                    }
                }
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









    // BEGIN OF NEW DATABASE FUNCTIONS

/*

    |
    |
    |
    |
   New Database Functions
    |
    |
    |
    |

*/


    /**
     * Returns true if the UUID is already in the Player Data
     * @param uuid
     * @return boolean
     */
    public static boolean isPlayerInDatabase(UUID uuid) {

        Connection checkPlayerExists = null;

        try {

            checkPlayerExists = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = checkPlayerExists.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");

                preparedStatement.setString(1, uuid.toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    return true;
                }

            } catch (SQLException e) {

                e.printStackTrace();

            } finally {
                if (checkPlayerExists != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(checkPlayerExists);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;

    }


    /**
     * Returns true if the UUID is in the active_server_bans table
     * @param uuid
     * @param servername
     * @return boolean
     */
    public static boolean isServerBanned(UUID uuid, String servername) {

        Connection checkPlayerExists = null;
        try {

            checkPlayerExists = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = checkPlayerExists.prepareStatement("SELECT * FROM active_server_bans WHERE uuid = ? && servername = ?");

                preparedStatement.setString(1, uuid.toString());

                preparedStatement.setString(2, servername);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    return true;

                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (checkPlayerExists != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(checkPlayerExists);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;

    }



    /**
     * Returns true if the UUID is in the active_global_bans table
     * @param uuid
     * @return boolean
     */
    public static boolean isGlobalBanned(UUID uuid) {

        Connection checkPlayerExists = null;
        try {

            checkPlayerExists = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = checkPlayerExists.prepareStatement("SELECT * FROM active_global_bans WHERE uuid = ?");

                preparedStatement.setString(1, uuid.toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (checkPlayerExists != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(checkPlayerExists);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;

    }





    /**
     * Returns true if the Saved Playername is the same as the given Playername
     * @param uuid
     * @param playername
     * @return boolean
     */
    public static boolean checkPlayerName(UUID uuid, String playername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM player_data WHERE uuid = ? && playername = ?");

                preparedStatement.setString(1, uuid.toString());

                preparedStatement.setString(2, playername);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }



    /**
     * Returns the Protected Boolean from the Database.
     * @param uuid UUID of the Player that should be checked.
     * @return boolean
     */
    public static boolean isProtected(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");

                preparedStatement.setString(1, uuid.toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    return resultSet.getBoolean("protected");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    /**
     * Returns the Banned Times for the given ID
     * @param uuid
     * @param id
     * @return int
     */
    public static int getBannedTimesForID(String uuid, String id) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM punishment_history WHERE punishuuid = ? && punishforid = ?");

                preparedStatement.setString(1, uuid);

                preparedStatement.setString(2, id);

                ResultSet resultSet = preparedStatement.executeQuery();

                int i = 1;

                while (resultSet.next()) {
                    i++;
                }

                return i;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }


    /**
     * Creates a new Player in the player_data table
     * @param uuid
     * @param playername
     */
    public static void createNewPlayer(UUID uuid, String playername, boolean hasBypassPermission) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement insertPlayer = conn.prepareStatement("INSERT INTO player_data (uuid, playername, protected) VALUES (?, ?, ?)");

                insertPlayer.setString(1, uuid.toString());

                insertPlayer.setString(2, playername);

                insertPlayer.setBoolean(3, hasBypassPermission);

                insertPlayer.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Creates a new Global Ban in the active_global_bans table
     * @param uuid
     * @param reason
     * @param bannedby
     * @param banneduntil
     */
    public static void newGlobalBan(String uuid, String reason, String bannedby, long banneduntil, int bannedforid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement insertGlobalBan = conn.prepareStatement("INSERT INTO active_global_bans (uuid, reason, banby, banuntil, banforid) VALUES (?, ?, ?, ?, ?)");

                insertGlobalBan.setString(1, uuid);

                insertGlobalBan.setString(2, reason);

                insertGlobalBan.setString(3, bannedby);

                insertGlobalBan.setLong(4, banneduntil);

                insertGlobalBan.setInt(5, bannedforid);

                insertGlobalBan.executeUpdate();

                PreparedStatement insertPunishmentHistory = conn.prepareStatement("INSERT INTO punishment_history (punishuuid, punishby, punishtype, punishat, punishuntil, punishforid) VALUES (?, ?, ?, ?, ?, ?)");

                insertPunishmentHistory.setString(1, uuid);

                insertPunishmentHistory.setString(2, bannedby);

                insertPunishmentHistory.setString(3, "global");

                insertPunishmentHistory.setLong(4, System.currentTimeMillis());

                insertPunishmentHistory.setLong(5, banneduntil);

                insertPunishmentHistory.setInt(6, bannedforid);

                insertPunishmentHistory.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    /**
     * Creates a new Server Ban in the active_server_bans table
     * @param uuid
     * @param reason
     * @param bannedby
     * @param servername
     * @param banneduntil
     * @param bannedforid
     */
    public static void newServerBan(String uuid, String reason, String bannedby, String servername, long banneduntil, int bannedforid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement insertServerBan = conn.prepareStatement("INSERT INTO active_server_bans (uuid, sbanreason, sbanby, servername, sbanuntil, sbanforid) VALUES (?, ?, ?, ?, ?, ?)");

                insertServerBan.setString(1, uuid);

                insertServerBan.setString(2, reason);

                insertServerBan.setString(3, bannedby);

                insertServerBan.setString(4, servername);

                insertServerBan.setLong(5, banneduntil);

                insertServerBan.setInt(6, bannedforid);

                insertServerBan.executeUpdate();


                PreparedStatement insertPunishmentHistory = conn.prepareStatement("INSERT INTO punishment_history (punishuuid, punishby, punishtype, punishat, punishuntil, punishforid) VALUES (?, ?, ?, ?, ?, ?)");

                insertPunishmentHistory.setString(1, uuid);

                insertPunishmentHistory.setString(2, bannedby);

                insertPunishmentHistory.setString(3, "server");

                insertPunishmentHistory.setLong(4, System.currentTimeMillis());

                insertPunishmentHistory.setLong(5, banneduntil);

                insertPunishmentHistory.setInt(6, bannedforid);

                insertPunishmentHistory.executeUpdate();


            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Creates a new Command Log
     * @param performedby
     * @param targetname
     * @param command
     */
    public static void newCommandLog(String performedby, String targetname, String command) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement insertCommandLog = conn.prepareStatement("INSERT INTO command_logs (perfby, perfto, command) VALUES (?, ?, ?)");

                insertCommandLog.setString(1, performedby);

                insertCommandLog.setString(2, targetname);

                insertCommandLog.setString(3, command);

                insertCommandLog.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Updates the PlayerName in the player_data table
     * @param uuid
     * @param playername
     */
    public static void updatePlayerName(UUID uuid, String playername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement updatePlayerData = conn.prepareStatement("UPDATE player_data SET playername = ? WHERE uuid = ?");

                updatePlayerData.setString(1, playername);

                updatePlayerData.setString(2, uuid.toString());

                updatePlayerData.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Updates the PlayerName in the player_data table
     * @param uuid
     * @param protectedboolean
     */
    public static void updateProtectedField(UUID uuid, boolean protectedboolean) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement updatePlayerData = conn.prepareStatement("UPDATE player_data SET protected = ? WHERE uuid = ?");

                updatePlayerData.setBoolean(1, protectedboolean);

                updatePlayerData.setString(2, uuid.toString());

                updatePlayerData.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Removes a Global ban from active_global_bans where the given UUID is the BANUUID
     * @param uuid
     */
    public static void deleteGlobalBan(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement deleteGlobalBan = conn.prepareStatement("DELETE FROM active_global_bans WHERE uuid = ?");

                deleteGlobalBan.setString(1, uuid.toString());

                deleteGlobalBan.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * Removes a Global ban from active_global_bans where the given UUID is the BANUUID
     * @param uuid
     */
    public static void deleteServerBan(UUID uuid, String servername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement deleteServerBan = conn.prepareStatement("DELETE FROM active_server_bans WHERE uuid = ? && servername = ?");

                deleteServerBan.setString(1, uuid.toString());

                deleteServerBan.setString(2, servername);

                deleteServerBan.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Gets the Ban History of the given UUID
     * @param uuid
     */
    public static void getHistory(Player player, String uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                int i = 1;

                PreparedStatement getHistory = conn.prepareStatement("SELECT * FROM punishment_history WHERE punishuuid = ? ORDER BY punishid DESC LIMIT 10");

                getHistory.setString(1, uuid);

                ResultSet resultSet = getHistory.executeQuery();

                player.sendMessage(Component.text("§cLAST 10 BAN HISTORY OF: §a" + getName(uuid).toUpperCase()));

                while (resultSet.next()) {


                    player.sendMessage(Component.text(" "));

                    player.sendMessage(Component.text("§cPunishment ID: §a" + resultSet.getInt("punishid")));

                    player.sendMessage(Component.text("§cPunished UUID: §a" + resultSet.getString("punishuuid")));

                    player.sendMessage(Component.text("§cPunished By: §a" + resultSet.getString("punishby")));

                    player.sendMessage(Component.text("§cPunishment Type: §a" + resultSet.getString("punishtype")));

                    player.sendMessage(Component.text("§cPunished At: §a" + resultSet.getLong("punishat")));

                    player.sendMessage(Component.text("§cPunished Until: §a" + resultSet.getLong("punishuntil")));

                    player.sendMessage(Component.text("§cPunished For: §a" + BanSystem.getVelocityConfig().getReason(String.valueOf(resultSet.getInt("punishforid")))));

                    player.sendMessage(Component.text(" "));

                }

                player.sendMessage(Component.text("§cEND OF LAST 10 BAN HISTORY OF: §a" + getName(uuid).toUpperCase()));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Gets the x Last logs of the given number
     * @param limit the number how many logs should be shown.
     */
    public static void getLogs(Player player, int limit) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                int i = 1;

                PreparedStatement getHistory = conn.prepareStatement("SELECT * FROM command_logs ORDER BY logid DESC LIMIT ?");

                getHistory.setInt(1, limit);

                ResultSet resultSet = getHistory.executeQuery();

                player.sendMessage(Component.text("§cLAST "+ limit +" COMMAND LOGS"));

                while (resultSet.next()) {

                    player.sendMessage(Component.text(" "));

                    player.sendMessage(Component.text("§cPerformed By: §a" + resultSet.getString("perfby")));

                    player.sendMessage(Component.text("§cTarget: §a" + resultSet.getString("perfto")));

                    player.sendMessage(Component.text("§cCommand: §a" + resultSet.getString("command")));

                    player.sendMessage(Component.text("§cTimestamp: §a" + resultSet.getString("timestamp")));

                    player.sendMessage(Component.text(" "));

                }

                player.sendMessage(Component.text("§cEND OF LAST "+ limit +" COMMAND LOGS"));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Gets the UUID for the given Player name when already in Database
     * @param name
     * @return UUID or null
     */
    public static UUID getUUID(String name) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement getUUID = conn.prepareStatement("SELECT uuid FROM player_data WHERE playername = ?");

                getUUID.setString(1, name);

                ResultSet resultSet = getUUID.executeQuery();

                while (resultSet.next()) {

                    return UUID.fromString(resultSet.getString("uuid"));

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    /**
     * Gets the UUID for the given Player name when already in Database
     * @param uuid
     * @return Playername or null
     */
    public static String getName(String uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement getUUID = conn.prepareStatement("SELECT playername FROM player_data WHERE uuid = ?");

                getUUID.setString(1, uuid);

                ResultSet resultSet = getUUID.executeQuery();

                while (resultSet.next()) {

                    return resultSet.getString("playername");

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    /**
     * Returns a ResultSet with the Global BanData for the given UUID need to check before if the player is banned
     * @param uuid
     * @return ResultSet or null
     */
    public static ResultSet requestGlobalBanData(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement requestGlobalBanData = conn.prepareStatement("SELECT * FROM active_global_bans WHERE uuid = ?");

                requestGlobalBanData.setString(1, uuid.toString());

                ResultSet resultSet = requestGlobalBanData.executeQuery();

                while (resultSet.next()) {

                    return resultSet;

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;

    }

    /**
     * Returns a ResultSet with the Server BanData for the given UUID need to check before if the player is banned
     * @param uuid
     * @return ResultSet or null
     */
    public static ResultSet requestServerBanData(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement requestGlobalBanData = conn.prepareStatement("SELECT * FROM active_server_bans WHERE uuid = ?");

                requestGlobalBanData.setString(1, uuid.toString());

                ResultSet resultSet = requestGlobalBanData.executeQuery();

                while (resultSet.next()) {

                    return resultSet;

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;

    }


}
