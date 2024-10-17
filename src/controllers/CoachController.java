package controllers;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CoachController {

    // Method to authenticate the coach
    public static boolean authenticate(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection(); // Adjust this according to your database connection method
            String query = "SELECT * FROM coaches WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if a matching record is found
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if there's an error
        }
    }
}
