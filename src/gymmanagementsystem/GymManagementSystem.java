package gymmanagementsystem;

import controllers.AdminController;
import controllers.CoachController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import views.dashboard.AdminDashboard;
import views.dashboard.CoachDashboard;

public class GymManagementSystem extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gym Management System - Login");

        // Create layout (VBox to stack elements vertically and center them)
        VBox vbox = new VBox(20); // Reduced spacing for compact design
        vbox.setPadding(new Insets(20)); // Reduced padding around the vbox
        vbox.setAlignment(Pos.CENTER); // Center all elements in the VBox

        // Add a modern dark background
        vbox.setStyle("-fx-background-color: #1D1F21;"); // Dark background

        // Add logo with updated shadow
        Image logo = new Image("file:resources/logo.jpg");
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(150);
        logoView.setFitHeight(150);

        // Apply shadow effect for a modern look
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5)); // Slightly darker shadow
        logoView.setEffect(dropShadow);
        vbox.getChildren().add(logoView); // Add the logo to the VBox

        // Create GridPane for Username and Password fields
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15); // Reduced gap for compactness
        grid.setVgap(15); // Reduced gap for compactness

        // Username
        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: #ECF0F1; -fx-font-size: 16px;");
        grid.add(userLabel, 0, 0);

        TextField userText = new TextField();
        userText.setPrefWidth(220);  // Slightly wider input field
        userText.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-background-color: #34495E; -fx-text-fill: #ECF0F1;");
        grid.add(userText, 1, 0);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: #ECF0F1; -fx-font-size: 16px;");
        grid.add(passwordLabel, 0, 1);

        PasswordField passwordText = new PasswordField();
        passwordText.setPrefWidth(220);
        passwordText.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-background-color: #34495E; -fx-text-fill: #ECF0F1;");
        grid.add(passwordText, 1, 1);

        // Add GridPane to VBox
        vbox.getChildren().add(grid);

        // Buttons (inside a horizontal layout for side-by-side buttons)
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(30);

        // Admin Login Button
        Button adminLoginButton = createLoginButton("Admin Login", "#2980B9", primaryStage, userText, passwordText, true);
        buttonGrid.add(adminLoginButton, 0, 0);

        // Coach Login Button
        Button coachLoginButton = createLoginButton("Coach Login", "#8E44AD", primaryStage, userText, passwordText, false);
        buttonGrid.add(coachLoginButton, 1, 0);

        // Add buttons grid to the VBox
        vbox.getChildren().add(buttonGrid);



        // Create the scene and add the layout
        Scene scene = new Scene(vbox, 400, 420); // Smaller window for compact UI
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to create a login button with hover effect (box-shadow only)
    private Button createLoginButton(String text, String normalColor, Stage primaryStage, TextField userText, PasswordField passwordText, boolean isAdmin) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: " + normalColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 20px;");

        // Apply box-shadow hover effect
        button.setOnMouseEntered(e -> button.setEffect(new DropShadow(10, Color.BLACK)));
        button.setOnMouseExited(e -> button.setEffect(null));  // Remove shadow when mouse exits

        button.setOnAction(e -> {
            String username = userText.getText();
            String password = passwordText.getText();
            boolean success;

            if (isAdmin) {
                success = AdminController.authenticate(username, password);
            } else {
                success = CoachController.authenticate(username, password);
            }

            if (success) {
                showAlert(AlertType.INFORMATION, (isAdmin ? "Admin" : "Coach") + " login successful!", "Welcome to the Gym Management System!");
                primaryStage.close();
                if (isAdmin) {
                    new AdminDashboard().start(new Stage());
                } else {
                    new CoachDashboard().start(new Stage());
                }
            } else {
                showAlert(AlertType.ERROR, "Invalid username or password!", "Please check your credentials and try again.");
            }
        });

        return button;
    }

    // Helper method to display an alert dialog
    private void showAlert(AlertType alertType, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
