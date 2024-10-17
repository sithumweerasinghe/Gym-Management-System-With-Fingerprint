package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_LOCAL_URL = "jdbc:mysql://localhost:3306/gym_management_system"; // Local DB URL
    private static final String DB_ONLINE_URL = "jdbc:mysql://your_online_db_url:3306/gym_management_system"; // Online DB URL
    private static final String DB_USERNAME = "root"; // Your DB username
    private static final String DB_PASSWORD = ""; // Your DB password

    private static Connection connection = null;
    private static final boolean USE_ONLINE_DB = false; // Change to 'true' for online DB

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String dbUrl = USE_ONLINE_DB ? DB_ONLINE_URL : DB_LOCAL_URL;
            connection = DriverManager.getConnection(dbUrl, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connected to the database.");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while closing the connection.");
        }
    }
}



