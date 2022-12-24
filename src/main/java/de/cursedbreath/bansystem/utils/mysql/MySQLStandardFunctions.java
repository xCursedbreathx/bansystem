package de.cursedbreath.bansystem.utils.mysql;

import de.cursedbreath.bansystem.BanSystem;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class MySQLStandardFunctions {

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

    public static UUID getUUID(String name) {
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
        }
    }

    public static String getNAME(String uuid) {
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
        }
    }

    public static void insertBAN(String uuid, String playername, String reason, String bannedby, Long duration) {
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
        }
    }

    public static void deleteBAN(String uuid) {
        Connection conn = null;
        try {
            conn = BanSystem.getMySQLConnectionPool().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM active_bans WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkBAN(String uuid) {
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
        }
    }

    public static ResultSet getBAN(String uuid) {
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
        }
    }

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
