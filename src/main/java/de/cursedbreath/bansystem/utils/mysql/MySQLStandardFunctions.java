package de.cursedbreath.bansystem.utils.mysql;

import com.velocitypowered.api.proxy.Player;
import de.cursedbreath.bansystem.BanSystem;
import de.cursedbreath.bansystem.utils.GlobalVariables;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.InputStream;
import java.sql.*;
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
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
     * Gets the Player UUID for the give Playername
     * @param name
     * @return
     */
    public static UUID getUUID(String name) throws SQLException {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT uuid FROM player_data WHERE playername = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return UUID.fromString(resultSet.getString("uuid"));
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                BanSystem.getMySQLConnectionPool().returnConnection(conn);
            }
        }
    }

    /**
     * Gets the Informations for the Banned Screen
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
        } catch (SQLException | ClassNotFoundException e) {
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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
