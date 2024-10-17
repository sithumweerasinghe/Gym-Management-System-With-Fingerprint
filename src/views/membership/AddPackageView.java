package views.membership;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddPackageView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Add Package");

        // Create the form
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(15);
        form.setHgap(10);

        // Package Name
        TextField packageNameField = new TextField();
        form.add(new Label("Package Name:"), 0, 0);
        form.add(packageNameField, 1, 0);

        // Package Duration
        TextField packageDurationField = new TextField();
        form.add(new Label("Duration (days):"), 0, 1);
        form.add(packageDurationField, 1, 1);

        // Package Description
        TextArea packageDescriptionField = new TextArea();
        packageDescriptionField.setPrefRowCount(3);
        form.add(new Label("Description:"), 0, 2);
        form.add(packageDescriptionField, 1, 2);

        // Package Price
        TextField packagePriceField = new TextField();
        form.add(new Label("Price ($):"), 0, 3);
        form.add(packagePriceField, 1, 3);

        // Submit Button
        Button submitButton = new Button("Add Package");
        submitButton.setOnAction(e -> submitForm(
                packageNameField.getText(),
                packageDurationField.getText(),
                packageDescriptionField.getText(),
                packagePriceField.getText()
        ));
        form.add(submitButton, 1, 4);

        // Set scene and stage
        Scene scene = new Scene(form, 650, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to handle form submission with validation
    private void submitForm(String name, String duration, String description, String price) {
        // Validate input fields
        String validationMessage = validateInput(name, duration, price);

        if (!validationMessage.isEmpty()) {
            // Show validation error message
            Alert alert = new Alert(Alert.AlertType.ERROR, validationMessage);
            alert.show();
            return;
        }

        // Insert package into the database
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO packages (name, duration, description, price) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, duration);
            statement.setString(3, description);
            statement.setDouble(4, Double.parseDouble(price));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Package added successfully!");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding package to database.");
            alert.show();
        } finally {
            DBConnection.closeConnection();  // Always close the connection after the operation
        }
    }

    // Method to validate input fields
    private String validateInput(String name, String duration, String price) {
        StringBuilder errorMessage = new StringBuilder();

        // Check if package name is empty
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Package Name cannot be empty.\n");
        }

        // Check if duration is a valid number
        try {
            int parsedDuration = Integer.parseInt(duration);
            if (parsedDuration <= 0) {
                errorMessage.append("Duration must be a positive integer.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Duration must be a valid integer.\n");
        }

        // Check if price is a valid number
        try {
            double parsedPrice = Double.parseDouble(price);
            if (parsedPrice <= 0) {
                errorMessage.append("Price must be a positive number.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Price must be a valid number.\n");
        }

        return errorMessage.toString();
    }
}
