package de.cursedbreath.bansystem.utils.mysql;

import com.velocitypowered.api.proxy.Player;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MySQLFunctions {

    //Database Update Functions

    /**
     * Updates the playername in the database
     * @param name
     * @param uuid
     * @throws SQLException
     */
    public static void updatePLAYERNAME(String name, String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement statement = conn.prepareStatement("UPDATE player_data SET name=? WHERE uuid=?");
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }



    //Database Insert Functions

    /**
     * Inserting a new Player into player_data table
     * @param uuid
     * @param name
     * @throws SQLException
     */
    public static void insertPLAYER(String uuid, String name) throws SQLException {

        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement statement = conn.prepareStatement("INSERT INTO player_data (uuid, playername) VALUES (?, ?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Adds a new Ban into the Database
     * @param uuid
     * @param playername
     * @param reason
     * @param bannedby
     * @param duration
     */
    public static void insertBAN(String uuid, String playername, String reason, String bannedby, Long duration) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO active_bans (uuid, playername, reason, bannedby, banneduntil) VALUES ( ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, playername);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, bannedby);
            preparedStatement.setLong(5, duration);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Adds a new History entry into the Database
     * @param uuid
     * @param playername
     * @param reason
     * @param bannedby
     * @param duration
     */
    public static void insertHistory(String uuid, String playername, String reason, String bannedby, Long duration) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO ban_history (uuid, playername, reason, bannedby, bannedat, banneduntil) VALUES ( ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, playername);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, bannedby);
            preparedStatement.setLong(5, System.currentTimeMillis());
            preparedStatement.setLong(6, duration);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }



    //Database Delete Functions

    /**
     * Deletes the ban with given UUID
     * @param uuid
     */
    public static void deleteBAN(String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM active_bans WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }



    //Database Checks

    /**
     * Checks if uuid is Banned.
     * @param uuid
     * @return boolean
     */
    public static boolean checkBAN(String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM active_bans WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Checks if the Player exists in the Database.
     * @param uuid
     * @return
     * @throws SQLException
     */
    public static boolean playerExists(String uuid) throws SQLException {

        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
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

        return false;
    }



    //Database Getter Functions

    /**
     * Gets the name of a Player from the Database.
     * @param uuid
     * @return
     */
    public static String getNAME(String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT playername FROM player_data WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("playername");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Gets the Information for the Banned Screen
     * @param uuid
     * @return
     */
    public static ResultSet getBAN(String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM active_bans WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Get the Count how many Times a player has been banned before with the same reason.
     * @param uuid
     * @param reason
     * @return
     * @throws SQLException
     */

    public static int getBannedTimes(String uuid, String reason) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ban_history WHERE uuid = ? AND reason = ?");
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, reason);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 1;
            while (resultSet.next()) {
                i++;
            }
            return i;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }


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
     * @return
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
     * @return
     */
    public static boolean isServerBanned(UUID uuid, String servername) {

        Connection checkPlayerExists = null;
        try {

            checkPlayerExists = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = checkPlayerExists.prepareStatement("SELECT * FROM active_server_bans WHERE SBANUUID = ?");

                preparedStatement.setString(1, uuid.toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if(resultSet.getString("SERVERNAME").contains(servername)) {
                        return true;
                    }
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
     * @return
     */
    public static boolean isGlobalBanned(UUID uuid) {

        Connection checkPlayerExists = null;
        try {

            checkPlayerExists = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = checkPlayerExists.prepareStatement("SELECT * FROM active_global_bans WHERE SBANUUID = ?");

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
     * @return
     */
    public static boolean checkPlayerName(String uuid, String playername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM player_data WHERE UUID = ?");

                preparedStatement.setString(1, uuid);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if(resultSet.getString("PLAYERNAME").equalsIgnoreCase(playername)) {
                        return true;
                    }
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


    public static int getBannedTimesForID(String uuid, String id) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM punishmentHistory WHERE PUNISHUUID = ? && PUNISHFORID = ?");

                preparedStatement.setString(1, uuid);

                preparedStatement.setString(2, id);

                ResultSet resultSet = preparedStatement.executeQuery();

                int i = 0;

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
        return 0;
    }


    /**
     * Creates a new Player in the player_data table
     * @param uuid
     * @param playername
     */
    public static void createNewPlayer(UUID uuid, String playername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement insertPlayer = conn.prepareStatement("INSERT INTO player_data (UUID, NAME) VALUES (?, ?)");

                insertPlayer.setString(1, uuid.toString());

                insertPlayer.setString(2, playername);

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

                PreparedStatement insertGlobalBan = conn.prepareStatement("INSERT INTO active_global_bans (SBANUUID, REASON, BANNEDBY, BANNEDUNTIL, BANNEDFORID) VALUES (?, ?, ?, ?, ?)");

                insertGlobalBan.setString(1, uuid);

                insertGlobalBan.setString(2, reason);

                insertGlobalBan.setString(3, bannedby);

                insertGlobalBan.setLong(4, banneduntil);

                insertGlobalBan.setInt(5, bannedforid);

                insertGlobalBan.executeUpdate();

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

                PreparedStatement insertServerBan = conn.prepareStatement("INSERT INTO active_server_bans (SBANUUID, REASON, SBANBY, SERVERNAME, SBANUNTIL, SBANFORID) VALUES (?, ?, ?, ?, ?, ?)");

                insertServerBan.setString(1, uuid);

                insertServerBan.setString(2, reason);

                insertServerBan.setString(3, bannedby);

                insertServerBan.setString(4, servername);

                insertServerBan.setLong(5, banneduntil);

                insertServerBan.setInt(6, bannedforid);

                insertServerBan.executeUpdate();

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

                PreparedStatement insertCommandLog = conn.prepareStatement("INSERT INTO command_logs (PERFDBY, PERFTO, COMMAND) VALUES (?, ?, ?)");

                insertCommandLog.setString(1, performedby);

                insertCommandLog.setString(2, targetname);

                insertCommandLog.setString(3, command);

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
    public static void updatePlayerName(String uuid, String playername) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement updatePlayerData = conn.prepareStatement("UPDATE player_data SET PLAYERNAME = ? WHERE PLAYERID = ?");

                updatePlayerData.setString(1, playername);

                updatePlayerData.setString(2, uuid);

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
     * Gets the Ban History of the given UUID
     * @param uuid
     * @return
     */
    public static void getHistory(Player player, String uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                int i = 1;

                PreparedStatement getHistory = conn.prepareStatement("SELECT * FROM punishment_history WHERE UUID = ? LIMIT 10");

                getHistory.setString(1, uuid);

                ResultSet resultSet = getHistory.executeQuery();

                player.sendMessage(Component.text("--------- LAST 10 BAN HISTORY OF: " + player.getUsername().toUpperCase() + " ---------"));

                while (resultSet.next()) {


                    player.sendMessage(Component.text(" "));

                    player.sendMessage(Component.text("Punishment ID: " + resultSet.getInt("PUNISHID")));

                    player.sendMessage(Component.text("Punished UUID: " + resultSet.getString("PUNISHUUID")));

                    player.sendMessage(Component.text("Punished By: " + resultSet.getString("PUNISHBY")));

                    player.sendMessage(Component.text("Punishment Type: " + resultSet.getString("PUNISHTYPE")));

                    player.sendMessage(Component.text("Punished At: " + resultSet.getLong("PUNISHAT")));

                    player.sendMessage(Component.text("Punished Until: " + resultSet.getLong("PUNISHUNTIL")));

                    player.sendMessage(Component.text("Punished For: " + BanSystem.getVelocityConfig().getReason(String.valueOf(resultSet.getInt("PUNISHFORID")))));

                    player.sendMessage(Component.text(" "));

                }

                player.sendMessage(Component.text("--------- END OF LAST 10 BAN HISTORY OF: " + player.getUsername().toUpperCase() + " ---------"));

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
     * @return
     */
    public static UUID getUUID(String name) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement getUUID = conn.prepareStatement("SELECT PLAYERID FROM player_data WHERE PLAYERNAME = ?");

                getUUID.setString(1, name);

                ResultSet resultSet = getUUID.executeQuery();

                while (resultSet.next()) {

                    return UUID.fromString(resultSet.getString("PLAYERID"));

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
     * @return
     */
    public static ResultSet requestGlobalBanData(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement requestGlobalBanData = conn.prepareStatement("SELECT * FROM active_global_bans WHERE BANUUID = ?");

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
     * @return
     */
    public static ResultSet requestServerBanData(UUID uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement requestGlobalBanData = conn.prepareStatement("SELECT * FROM active_server_bans WHERE SBANUUID = ?");

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


    public static void deleteGlobalBan(String uuid) {

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement deleteGlobalBan = conn.prepareStatement("DELETE FROM active_global_bans WHERE BANUUID = ?");

                deleteGlobalBan.setString(1, uuid);

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



}
