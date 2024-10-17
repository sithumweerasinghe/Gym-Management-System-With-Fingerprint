package views.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddUserController {

    // Method to load packages from the database into the ComboBox
    public void loadPackages(ComboBox<String> packageComboBox) {
        ObservableList<String> packageList = FXCollections.observableArrayList();

        String query = "SELECT name FROM packages"; // Query to fetch package names
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Loop through the result set and add package names to the list
            while (rs.next()) {
                packageList.add(rs.getString("name"));
            }

            // Set the items for packageComboBox
            packageComboBox.setItems(packageList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load packages: " + e.getMessage());
        }
    }

    // Method to submit user data to the database and return success/failure
    public boolean submitUser(String fullName, String address, LocalDate dob, String whatsapp, String email,
                              String status, String membershipPackage, String gender, String weight, String height) {
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts.length > 0 ? nameParts[0] : ""; // Get first name only

        // Calculate age from the date of birth
        int age = LocalDate.now().getYear() - dob.getYear();

        // Fetch the package ID from the database based on the package name
        int packageId = getPackageId(membershipPackage);

        // Check if the packageId is valid (-1 means it wasn't found)
        if (packageId == -1) {
            showAlert("Error", "Selected package not found.");
            return false;
        }

        // Database insertion logic
        String sql = "INSERT INTO users (first_name, gender, date_of_birth, email, address, status, " +
                "whatsapp, weight, height, package_id, join_date, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters for the PreparedStatement
            pstmt.setString(1, firstName);
            pstmt.setString(2, gender); // Set gender
            pstmt.setDate(3, java.sql.Date.valueOf(dob)); // Set date of birth
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setString(6, status);
            pstmt.setString(7, whatsapp); // Set WhatsApp number
            pstmt.setDouble(8, Double.parseDouble(weight)); // Convert weight to double
            pstmt.setDouble(9, Double.parseDouble(height)); // Convert height to double
            pstmt.setInt(10, packageId); // Set package ID
            pstmt.setDate(11, java.sql.Date.valueOf(LocalDate.now())); // Set join date to today
            pstmt.setInt(12, age); // Set age

            // Execute the update
            pstmt.executeUpdate();
            showAlert("Success", "User registered successfully!");
            return true; // Return true to indicate success

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to register user: " + e.getMessage());
            return false; // Return false to indicate failure
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid number format: " + e.getMessage());
            return false; // Return false to indicate failure
        }
    }

    // Method to get the package ID based on the package name
    private int getPackageId(String packageName) {
        String sql = "SELECT id FROM packages WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, packageName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return the package ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the package is not found
    }

    // Method to show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
