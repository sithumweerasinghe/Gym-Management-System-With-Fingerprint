package views.dashboard;

import controllers.AdminController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ProfileSettingsView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profile Settings");

        // Layout for the settings form
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(10);

        // Current username and password fields
        Label currentUsernameLabel = new Label("Current Username:");
        TextField currentUsernameField = new TextField(AdminController.getAdminUsername());
        currentUsernameField.setDisable(true);  // Disable editing the current username

        Label currentPasswordLabel = new Label("Current Password:");
        PasswordField currentPasswordField = new PasswordField();

        // Fields to update username and password
        Label newUsernameLabel = new Label("New Username:");
        TextField newUsernameField = new TextField();

        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();

        // Buttons
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText();
            if (AdminController.authenticate(AdminController.getAdminUsername(), currentPassword)) {
                String newUsername = newUsernameField.getText();
                String newPassword = newPasswordField.getText();
                if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                    AdminController.updateCredentials(newUsername, newPassword);
                    showAlert("Success", "Username and password updated successfully.");
                } else {
                    showAlert("Error", "New username and password cannot be empty.");
                }
            } else {
                showAlert("Error", "Current password is incorrect.");
            }
        });

        // Adding elements to the form
        form.add(currentUsernameLabel, 0, 0);
        form.add(currentUsernameField, 1, 0);
        form.add(currentPasswordLabel, 0, 1);
        form.add(currentPasswordField, 1, 1);
        form.add(newUsernameLabel, 0, 2);
        form.add(newUsernameField, 1, 2);
        form.add(newPasswordLabel, 0, 3);
        form.add(newPasswordField, 1, 3);
        form.add(saveButton, 1, 4);

        // Set up the scene
        Scene scene = new Scene(form, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
