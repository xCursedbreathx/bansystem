package de.cursedbreath.bansystem.utils.mysql;

import com.velocitypowered.api.proxy.Player;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.objects.HistoryObject;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class MySQLStandardFunctions {

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
     * Gets Ban History of player
     * @param player
     * @param uuid
     */
    public static void getHistory(Player player, String uuid) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ban_history WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            player.sendMessage(Component.text("§c§lBAN-HISTORY"));
            player.sendMessage(Component.text("--------------------", NamedTextColor.GRAY));
            while (resultSet.next()) {
                player.sendMessage(Component.text(" ", NamedTextColor.GRAY));
                player.sendMessage(Component.text("Reason: " + resultSet.getString("reason"), NamedTextColor.AQUA));
                player.sendMessage(Component.text("Banned by: " + resultSet.getString("bannedby"), NamedTextColor.AQUA));
                player.sendMessage(Component.text("Banned at: " + GlobalVariables.convertTime(resultSet.getLong("bannedat")), NamedTextColor.AQUA));
                player.sendMessage(Component.text("Banned until: " + GlobalVariables.convertTime(resultSet.getLong("banneduntil")), NamedTextColor.AQUA));
                player.sendMessage(Component.text(" ", NamedTextColor.GRAY));
            }
            player.sendMessage(Component.text("--------------------", NamedTextColor.GRAY));
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








    /**
     * Creates a new Player in the Database
     * @param uuid
     * @param playername
     */

    public static void newPlayer(String uuid, String playername) {
        Connection conn = null;

        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO player_data (uuid, playername) VALUES (?, ?)");

                preparedStatement.setString(1, uuid);

                preparedStatement.setString(2, playername);

                preparedStatement.executeUpdate();

            } catch (SQLException e) {

                e.printStackTrace();

            } finally {

                if (conn != null) {

                    BanSystem.getMySQLConnectionPool().returnConnection(conn);

                }

            }

        }catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Insert a Ban into the Database.
     * @param uuid uuid from the player that is getting banned
     * @param reason reason why the player is getting banned
     * @param bannedby the player that banned the player
     * @param type the type of the ban (global, server)
     * @param server the server the player is getting banned on or null if the ban is global
     * @param banneduntil the time until the player is banned
     * @param bannedforid the id the player have been banned for
     */
    public static void newBan(String uuid, String reason, String bannedby, String type, String server, long banneduntil, int bannedforid) {

        Connection conn = null;

        Connection insertBan = null;

        Connection updatePlayerData = null;

        Connection insertBanHistory = null;


        switch(type) {
            case "global":
                try {

                    insertBan = BanSystem.getMySQLConnectionPool().getConnection();

                    updatePlayerData = BanSystem.getMySQLConnectionPool().getConnection();

                    insertBanHistory = BanSystem.getMySQLConnectionPool().getConnection();

                    try {



                        //Start Inserting new Ban



                        PreparedStatement insertNewBan = insertBan.prepareStatement("INSERT INTO active_bans (UUID, REASON, BANBY, BANUNTIL, BANFORID) VALUES (?, ?, ?, ?, ?)");

                        insertNewBan.setString(1, uuid);

                        insertNewBan.setString(2, reason);

                        insertNewBan.setString(3, bannedby);

                        insertNewBan.setLong(4, banneduntil);

                        insertNewBan.setInt(5, bannedforid);

                        insertNewBan.executeUpdate();



                        //Start Updating Player Data



                        PreparedStatement changePlayerData = updatePlayerData.prepareStatement("UPDATE player_data SET GLOBALBANNED = ? WHERE UUID = ?");

                        changePlayerData.setBoolean(1, true);

                        changePlayerData.setString(2, uuid);

                        changePlayerData.executeUpdate();



                        //Start Inserting Ban History



                        PreparedStatement insertHistory = insertBanHistory.prepareStatement("INSERT INTO punishment_history (PUNISHUUID, PUNISHBY, PUNISHAT, PUNISHUNTIL, PUNSIHFORID) VALUES (?, ?, ?, ?, ?)");

                        insertHistory.setString(1, uuid);

                        insertHistory.setString(2, bannedby);

                        insertHistory.setLong(3, System.currentTimeMillis());

                        insertHistory.setLong(4, banneduntil);

                        insertHistory.setInt(5, bannedforid);

                        insertHistory.executeUpdate();

                    } catch (SQLException e) {

                        e.printStackTrace();

                    } finally {

                        if (insertBan != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(insertBan);

                        }

                        if (updatePlayerData != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(updatePlayerData);

                        }

                        if (insertBanHistory != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(insertBanHistory);

                        }

                    }

                }catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "server":
                try {

                    insertBan = BanSystem.getMySQLConnectionPool().getConnection();

                    updatePlayerData = BanSystem.getMySQLConnectionPool().getConnection();

                    insertBanHistory = BanSystem.getMySQLConnectionPool().getConnection();

                    try {



                        //Start Inserting new Ban



                        PreparedStatement insertNewBan = insertBan.prepareStatement("INSERT INTO active_bans (UUID, REASON, BANBY, BANTYPE, BANSERVER, BANUNTIL, BANFORID) VALUES (?, ?, ?, ?, ?, ?, ?)");

                        insertNewBan.setString(1, uuid);

                        insertNewBan.setString(2, reason);

                        insertNewBan.setString(3, bannedby);

                        insertNewBan.setString(4, type);

                        insertNewBan.setString(5, server);

                        insertNewBan.setLong(6, banneduntil);

                        insertNewBan.setInt(7, bannedforid);

                        insertNewBan.executeUpdate();


                        //Start Inserting Ban History



                        PreparedStatement insertHistory = insertBanHistory.prepareStatement("INSERT INTO punishment_history (PUNISHUUID, PUNISHBY, PUNISHTYPE, PUNISHAT, PUNISHUNTIL, PUNSIHFORID) VALUES (?, ?, ?, ?, ?)");

                        insertHistory.setString(1, uuid);

                        insertHistory.setString(2, bannedby);

                        insertHistory.setString(3, type);

                        insertHistory.setLong(4, System.currentTimeMillis());

                        insertHistory.setLong(5, banneduntil);

                        insertHistory.setInt(6, bannedforid);

                        insertHistory.executeUpdate();

                    } catch (SQLException e) {

                        e.printStackTrace();

                    } finally {

                        if (insertBan != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(insertBan);

                        }

                        if (updatePlayerData != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(updatePlayerData);

                        }

                        if (insertBanHistory != null) {

                            BanSystem.getMySQLConnectionPool().returnConnection(insertBanHistory);

                        }

                    }

                }catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }



    /**
     * Remove a Ban from the Database
     * @param uuid uuid from the player that is getting unbanned
     */
    public static void removeBanFromDatabase(String uuid) {
        Connection conn = null;
        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM active_bans WHERE UUID = ?");

                preparedStatement.setString(1, uuid);

                preparedStatement.executeUpdate();

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
     * Get the Count how many times a player has been banned for the given banid
     * @param uuid from the Player that should be checked
     * @param reasonid the reason id for what the player is getting banned
     * @return
     */
    public static int newGetBannedTimes(String uuid, int reasonid) {

        Connection conn = null;
        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT COUNT(*) FROM punishment_history WHERE PUNISHUUID = ? AND PUNSIHFORID = ?");

                preparedStatement.setString(1, uuid);
                preparedStatement.setInt(2, reasonid);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    return resultSet.getInt("COUNT(*)");
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

        return 0;

    }


    /**
     * Returns the Ban Data from the given UUID
     * @param uuid
     * @return
     */
    public static ResultSet getBanData(String uuid) {

        Connection conn = null;

        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM active_bans WHERE UUID = ?");

                preparedStatement.setString(1, uuid);

                ResultSet banData = preparedStatement.executeQuery();

                return banData;


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    BanSystem.getMySQLConnectionPool().returnConnection(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Returns the UUID from the given Name when the Name is in the Player Data otherwise it returns null
     * @param name
     * @return
     */
    public static String getUUIDFromName(String name) {

            Connection conn = null;

            try {

                conn = BanSystem.getMySQLConnectionPool().getConnection();

                try {

                    PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM player_data WHERE NAME = ?");

                    preparedStatement.setString(1, name);

                    ResultSet playerData = preparedStatement.executeQuery();

                    while (playerData.next()) {
                        return playerData.getString("UUID");
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
            return null;
    }



    //Database Booleans

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
    public static Map<String, Map<Integer, HistoryObject>> getHistory(String uuid) {

        Map<String, Map<Integer, HistoryObject>> cache = new HashMap<>();

        Map<Integer, HistoryObject> historyMap = new HashMap<>();

        Connection conn = null;

        try {

            conn = BanSystem.getMySQLConnectionPool().getConnection();

            try {

                int i = 1;

                PreparedStatement getHistory = conn.prepareStatement("SELECT * FROM punishment_history WHERE UUID = ?");

                getHistory.setString(1, uuid);

                ResultSet resultSet = getHistory.executeQuery();

                while (resultSet.next()) {

                    HistoryObject historyObject = new HistoryObject(resultSet.getInt("PUNISHID"),
                            resultSet.getString("PUNISHUUID"),
                            resultSet.getString("PUNISHBY"),
                            resultSet.getString("PUNISHTYPE"),
                            resultSet.getLong("PUNISHAT"),
                            resultSet.getLong("PUNISHUNTIL"),
                            resultSet.getInt("PUNISHFORID"));

                    historyMap.put(i, historyObject);

                }

                cache.put(uuid, historyMap);

                return cache;

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
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }





}
