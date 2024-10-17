package views.coach;

import fingerprint.FingerprintSDK;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.DBConnection;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddCoachView extends Application {

    private TabPane tabPane;
    private TableView<Coach> tableView = new TableView<>();
    private TextField firstNameField, lastNameField, usernameField, passwordField;
    private ComboBox<String> timePeriodCombo;
    private ComboBox<Integer> customDurationCombo;
    private ImageView profileImageView;
    private List<Coach> coachesList = new ArrayList<>();
    private Coach selectedCoach; // Store the selected coach for editing

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Coach Management");

        // Create the layout
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Create the tab pane
        tabPane = new TabPane();
        Tab addCoachTab = new Tab("Add New Coach", createAddCoachForm());
        Tab viewCoachesTab = new Tab("View Coaches", createViewCoachesTable());
        tabPane.getTabs().addAll(addCoachTab, viewCoachesTab);

        layout.setCenter(tabPane);

        // Set the scene
        Scene scene = new Scene(layout, 800, 600);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createAddCoachForm() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(15);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        // Create labels and fields
        Label firstNameLabel = new Label("First Name:");
        firstNameField = new TextField();
        Label lastNameLabel = new Label("Last Name:");
        lastNameField = new TextField();
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        Label timePeriodLabel = new Label("Available Time Period:");
        timePeriodCombo = new ComboBox<>();
        timePeriodCombo.getItems().addAll("Morning", "Afternoon", "Evening", "Night");
        Label customDurationLabel = new Label("Custom Duration (hours):");
        customDurationCombo = new ComboBox<>();
        customDurationCombo.getItems().addAll(1, 2, 5);
        Label profileImageLabel = new Label("Profile Photo:");
        Button uploadButton = new Button("Upload Photo");
        profileImageView = new ImageView();
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        profileImageView.setPreserveRatio(true);

        // Handle upload button
        uploadButton.setOnAction(e -> uploadPhoto());

        // Create buttons
        Button registerButton = new Button("Register Coach");
        styleButton(registerButton); // Apply style to button
        registerButton.setOnAction(e -> registerCoach());

        // Add components to the form
        form.add(firstNameLabel, 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(lastNameLabel, 0, 1);
        form.add(lastNameField, 1, 1);
        form.add(usernameLabel, 0, 2);
        form.add(usernameField, 1, 2);
        form.add(passwordLabel, 0, 3);
        form.add(passwordField, 1, 3);
        form.add(timePeriodLabel, 0, 4);
        form.add(timePeriodCombo, 1, 4);
        form.add(customDurationLabel, 0, 5);
        form.add(customDurationCombo, 1, 5);
        form.add(profileImageLabel, 0, 6);
        form.add(uploadButton, 1, 6);
        form.add(profileImageView, 1, 7);
        form.add(registerButton, 1, 8);

        return form;
    }

    private TableView<Coach> createViewCoachesTable() {
        createCoachTable();
        loadCoaches();
        return tableView;
    }

    private void createCoachTable() {
        TableColumn<Coach, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Coach, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Coach, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Coach, String> timePeriodColumn = new TableColumn<>("Available Time Period");
        timePeriodColumn.setCellValueFactory(new PropertyValueFactory<>("timePeriod"));

        TableColumn<Coach, Integer> customDurationColumn = new TableColumn<>("Custom Duration (hrs)");
        customDurationColumn.setCellValueFactory(new PropertyValueFactory<>("customDuration"));

        // Add buttons for update and delete
        TableColumn<Coach, Void> updateColumn = new TableColumn<>("Update");
        updateColumn.setCellFactory(col -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            {
                styleButton(updateButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                    updateButton.setOnAction(event -> updateCoach(getTableRow().getItem()));
                }
            }
        });

        TableColumn<Coach, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                styleRedButton(deleteButton); // Apply red style
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(event -> deleteCoach(getTableRow().getItem()));
                }
            }
        });

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, usernameColumn, timePeriodColumn, customDurationColumn, updateColumn, deleteColumn);
    }

    private void loadCoaches() {
        coachesList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM coaches")) {

            while (rs.next()) {
                Coach coach = new Coach(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("available_time_period"),
                        rs.getInt("custom_duration")
                );
                coachesList.add(coach);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load coaches.");
        }

        tableView.getItems().setAll(coachesList);
    }

    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    private void registerCoach() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String timePeriod = timePeriodCombo.getValue();
        Integer customDuration = customDurationCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || timePeriod == null || customDuration == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO coaches (first_name, last_name, username, password, available_time_period, custom_duration) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, username);
                pstmt.setString(4, password);
                pstmt.setString(5, timePeriod);
                pstmt.setInt(6, customDuration);
                pstmt.executeUpdate();
            }
            showAlert("Success", "Coach registered successfully!");
            clearInputFields();
            loadCoaches();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void updateCoach(Coach coach) {
        // Open a new dialog to edit coach details
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Coach");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameEdit = new TextField(coach.getFirstName());
        TextField lastNameEdit = new TextField(coach.getLastName());
        ComboBox<String> timePeriodEdit = new ComboBox<>();
        timePeriodEdit.getItems().addAll("Morning", "Afternoon", "Evening", "Night");
        timePeriodEdit.setValue(coach.getTimePeriod());
        ComboBox<Integer> durationEdit = new ComboBox<>();
        durationEdit.getItems().addAll(1, 2, 5);
        durationEdit.setValue(coach.getCustomDuration());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameEdit, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameEdit, 1, 1);
        grid.add(new Label("Available Time Period:"), 0, 2);
        grid.add(timePeriodEdit, 1, 2);
        grid.add(new Label("Custom Duration:"), 0, 3);
        grid.add(durationEdit, 1, 3);

        // Add Register Fingerprint Button
        Button registerFingerprintButton = new Button("Register Fingerprint");
        grid.add(registerFingerprintButton, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Register Fingerprint Action
        registerFingerprintButton.setOnAction(event -> {
            // Popup to capture fingerprint
            captureFingerprint(coach.getUsername());
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection connection = DBConnection.getConnection()) {
                    String sql = "UPDATE coaches SET first_name = ?, last_name = ?, available_time_period = ?, custom_duration = ? WHERE username = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, firstNameEdit.getText());
                        pstmt.setString(2, lastNameEdit.getText());
                        pstmt.setString(3, timePeriodEdit.getValue());
                        pstmt.setInt(4, durationEdit.getValue());
                        pstmt.setString(5, coach.getUsername());
                        pstmt.executeUpdate();
                    }
                    showAlert("Success", "Coach updated successfully!");
                    loadCoaches();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Database error: " + e.getMessage());
                }
            }
        });
    }

    // Method to capture fingerprint
    private byte[] captureFingerprint(String username) {
        showAlert("Loading", "Please scan your finger...");

        // Initialize the SDK
        if (!initializeFingerprintSDK()) {
            showAlert("Error", "Failed to initialize fingerprint SDK.");
            return new byte[0];
        }

        // Connect to the device (scan for devices within the network range)
        String deviceIP = scanForDevice("192.168.1.148", (short) 8000); // Update subnet and port as needed
        if (deviceIP == null) {
            showAlert("Error", "No fingerprint device found on the network.");
            cleanupSDK();
            return new byte[0];
        }

        // Login to the device
        int userID = attemptFingerprintLogin(deviceIP, (short) 8000);
        if (userID < 0) {
            showAlert("Error", "Failed to connect to the fingerprint device.");
            cleanupSDK();
            return new byte[0];
        }

        // Capture fingerprint data from the device
        byte[] fingerprintData = captureFingerprintFromDevice(userID);
        if (fingerprintData != null) {
            try (Connection connection = DBConnection.getConnection()) {
                String sql = "UPDATE coaches SET fingerprint_data = ? WHERE username = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setBytes(1, fingerprintData);
                    pstmt.setString(2, username);
                    pstmt.executeUpdate();
                    showAlert("Success", "Fingerprint registered successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database error: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Failed to capture fingerprint.");
        }

        // Clean up resources
        cleanupSDK(userID);
        return fingerprintData;
    }

    // Capture fingerprint data from the device using the correct SDK method
    private byte[] captureFingerprintFromDevice(int userID) {
        // Replace this with the actual SDK method to capture fingerprint
        byte[] fingerprintData = new byte[512]; // Example buffer size, adjust as per SDK docs
        boolean success = FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_CaptureFingerprint(userID, fingerprintData);

        if (!success) {
            System.err.println("Failed to capture fingerprint. Error code: " + FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            return null;
        }
        return fingerprintData;
    }

    // Initialize the fingerprint SDK
    private boolean initializeFingerprintSDK() {
        boolean initSuccess = FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_Init();
        if (!initSuccess) {
            System.err.println("SDK Initialization failed.");
        }
        return initSuccess;
    }

    // Helper function to scan for the device in the network
    private String scanForDevice(String subnet, short port) {
        // Implement logic for scanning devices on the network (e.g., by pinging each IP in the subnet)
        // Return device IP if found, otherwise return null
        return "192.168.1.100"; // Example IP, replace with actual scanning logic
    }

    // Attempt to log into the fingerprint device
    private int attemptFingerprintLogin(String deviceIP, short port) {
        int userID = FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_Login_V30(deviceIP, port, "admin", "password123", null);
        if (userID < 0) {
            System.err.println("Login failed. Error code: " + FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
        return userID;
    }

    // Cleanup resources when done
    private void cleanupSDK(int userID) {
        FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_Logout(userID);
        FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_Cleanup();
    }

    // Overloaded cleanup for general SDK cleanup when login fails
    private void cleanupSDK() {
        FingerprintSDK.HCNetSDK.INSTANCE.NET_DVR_Cleanup();
    }
    private void deleteCoach(Coach coach) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this coach?");
        confirmationAlert.setContentText("This action cannot be undone.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (coach != null) {
                    try (Connection connection = DBConnection.getConnection()) {
                        String sql = "DELETE FROM coaches WHERE username = ?";
                        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                            pstmt.setString(1, coach.getUsername());
                            pstmt.executeUpdate();
                        }
                        showAlert("Success", "Coach deleted successfully!");
                        loadCoaches();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Error", "Database error: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void clearInputFields() {
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
        timePeriodCombo.setValue(null);
        customDurationCombo.setValue(null);
        profileImageView.setImage(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #9838fc; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px;");
    }

    private void styleRedButton(Button button) {
        button.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px;");
    }
}