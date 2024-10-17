package views.user;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AddUserView extends Application {

    private ComboBox<String> packageComboBox; // Declare packageComboBox at the class level
    private AddUserController controller;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Add User");

        // Create controller
        controller = new AddUserController();

        // Create VBox for modern layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4;");

        // Title
        Label titleLabel = new Label("Register New User");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        titleLabel.setAlignment(Pos.CENTER);

        // Profile Icon
        Image profileImage = new Image("file:resources/profile.png"); // Add the path to your profile icon
        ImageView profileIcon = new ImageView(profileImage);
        profileIcon.setFitHeight(100);
        profileIcon.setFitWidth(100);

        // Add title and profile icon to VBox
        vbox.getChildren().addAll(titleLabel, profileIcon);

        // Create GridPane for the form
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER); // Center the form elements

        // Full Name
        Label fullNameLabel = new Label("Full Name:");
        TextField fullNameField = new TextField();
        grid.add(fullNameLabel, 0, 0);
        grid.add(fullNameField, 1, 0);

        // Address
        Label addressLabel = new Label("Full Address:");
        TextField addressField = new TextField();
        grid.add(addressLabel, 0, 1);
        grid.add(addressField, 1, 1);

        // Date of Birth
        Label dobLabel = new Label("Date of Birth:");
        DatePicker dobPicker = new DatePicker();
        grid.add(dobLabel, 0, 2);
        grid.add(dobPicker, 1, 2);

        // WhatsApp Number
        Label whatsappLabel = new Label("WhatsApp Number:");
        TextField whatsappField = new TextField();
        whatsappField.setPromptText("Enter number (only digits)"); // Set prompt text for user guidance
        whatsappField.setText("+94 "); // Set default country code (+94) in the field
        grid.add(whatsappLabel, 0, 3);
        grid.add(whatsappField, 1, 3);

        // Email
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        grid.add(emailLabel, 0, 4);
        grid.add(emailField, 1, 4);

        // Status
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Single", "Married", "Divorced", "Other");
        grid.add(statusLabel, 0, 5);
        grid.add(statusComboBox, 1, 5);

        // Membership Package
        Label packageLabel = new Label("Membership Package:");
        packageComboBox = new ComboBox<>();
        controller.loadPackages(packageComboBox);  // Load packages into combo box
        grid.add(packageLabel, 0, 6);
        grid.add(packageComboBox, 1, 6);

        // Gender
        Label genderLabel = new Label("Gender:");
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("Male");
        maleRadio.setToggleGroup(genderGroup);
        RadioButton femaleRadio = new RadioButton("Female");
        femaleRadio.setToggleGroup(genderGroup);
        grid.add(genderLabel, 0, 7);
        grid.add(maleRadio, 1, 7);
        grid.add(femaleRadio, 1, 8);

        // Weight
        Label weightLabel = new Label("Weight (kg):");
        TextField weightField = new TextField();
        grid.add(weightLabel, 0, 9);
        grid.add(weightField, 1, 9);

        // Height
        Label heightLabel = new Label("Height (cm):");
        TextField heightField = new TextField();
        grid.add(heightLabel, 0, 10);
        grid.add(heightField, 1, 10);

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            // Collect all user input and validate
            String whatsapp = whatsappField.getText().trim();
            if (!isValidWhatsAppNumber(whatsapp)) {
                showAlert("Error", "Please enter a valid WhatsApp number (only digits, 10-15 characters).");
                return;
            }

            // Remove the country code part and submit the number
            String finalWhatsAppNumber = whatsapp.replace("+94 ", "");

            // Call submitUser and pass all the user input
            boolean success = controller.submitUser(
                    fullNameField.getText(),
                    addressField.getText(),
                    dobPicker.getValue(),
                    finalWhatsAppNumber, // Pass the cleaned WhatsApp number
                    emailField.getText(),
                    statusComboBox.getValue(),
                    packageComboBox.getValue(),
                    maleRadio.isSelected() ? "Male" : "Female",
                    weightField.getText(),
                    heightField.getText()
            );

            if (success) {
                primaryStage.close(); // Only close if the submission was successful
            }
        });

        // Add form and submit button to VBox
        vbox.getChildren().addAll(grid, submitButton);

        // ScrollPane to make the form scrollable
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Scene and Stage
        Scene scene = new Scene(scrollPane, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to validate WhatsApp number
    private boolean isValidWhatsAppNumber(String whatsapp) {
        return whatsapp.matches("\\+94 \\d{9,12}"); // Validate for +94 and a 9-12 digit number
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
