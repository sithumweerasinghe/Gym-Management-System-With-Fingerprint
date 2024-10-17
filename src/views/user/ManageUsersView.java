package views.user;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import utils.DBConnection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersView extends Application {

    private TableView<User> userTable;
    private TextField searchField;
    private ObservableList<User> userList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Manage Users");

        // TableView setup
        userTable = new TableView<>();
        userList = getUsers(); // Load initial user data
        userTable.setItems(userList); // Bind the data to the TableView

        TableColumn<User, String> nameColumn = new TableColumn<>("Full Name");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFullName()));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> packageColumn = new TableColumn<>("Package");
        packageColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(getPackageNameById(cellData.getValue().getPackageId())));

        TableColumn<User, Double> weightColumn = new TableColumn<>("Weight (kg)");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<User, Double> heightColumn = new TableColumn<>("Height (cm)");
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<User, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= userList.size()) {
                    setGraphic(null);
                } else {
                    User user = userList.get(getIndex());
                    viewButton.setOnAction(e -> viewUserDetails(user));
                    updateButton.setOnAction(e -> updateUser(user));
                    deleteButton.setOnAction(e -> deleteUser(user));
                    updateButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 5px;");
                    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5px;");
                    setGraphic(new HBox(viewButton, updateButton, deleteButton));
                }
            }
        });

        userTable.getColumns().addAll(nameColumn, emailColumn, packageColumn, weightColumn, heightColumn, actionColumn);

        // Search functionality
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPadding(new Insets(5)); // Add padding to search field
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            ObservableList<User> filteredList = FXCollections.observableArrayList();
            for (User user : userList) {
                if (user.getFullName().toLowerCase().contains(newText.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            userTable.setItems(filteredList);
        });

        // Export button
        Button exportButton = new Button("Export Users");
        exportButton.setPadding(new Insets(5)); // Add padding to export button
        exportButton.setOnAction(e -> exportUsers());

        // Layout
        BorderPane layout = new BorderPane();
        layout.setCenter(userTable);
        layout.setTop(new HBox(10, searchField, exportButton));
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<User> getUsers() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String gender = rs.getString("gender");
                LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
                String email = rs.getString("email");
                String address = rs.getString("address");
                String status = rs.getString("status");
                String whatsappNumber = rs.getString("whatsapp");
                double weight = rs.getDouble("weight");
                double height = rs.getDouble("height");
                int packageId = rs.getInt("package_id");
                LocalDate joinDate = rs.getDate("join_date").toLocalDate();
                int age = rs.getInt("age");

                userList.add(new User(userId, firstName, gender, dateOfBirth, email, address, status, whatsappNumber, weight, height, packageId, joinDate, age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    private String getPackageNameById(int packageId) {
        String packageName = "N/A"; // Default value if not found
        String sql = "SELECT name FROM packages WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, packageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                packageName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    private void viewUserDetails(User user) {
        Stage detailStage = new Stage();
        detailStage.setTitle("User Details");
        detailStage.initModality(Modality.APPLICATION_MODAL);

        // User Details Labels with modern design
        Label nameLabel = new Label("Full Name: " + user.getFullName());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label whatsappLabel = new Label("WhatsApp: " + user.getWhatsappNumber());
        Label weightLabel = new Label("Weight: " + user.getWeight() + " kg");
        Label heightLabel = new Label("Height: " + user.getHeight() + " cm");
        Label packageLabel = new Label("Package: " + getPackageNameById(user.getPackageId()));

        // Style the labels
        styleLabels(nameLabel, emailLabel, whatsappLabel, weightLabel, heightLabel, packageLabel);

        // File list with download buttons
        VBox fileBox = new VBox(10);
        List<File> userFiles = getUserFiles(user.getId());  // Function to retrieve files from the DB
        for (File file : userFiles) {
            HBox fileRow = new HBox(10);
            fileRow.setAlignment(Pos.CENTER_LEFT); // Align items in the center left

            // Button to download file
            Button downloadButton = new Button("Download");
            downloadButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"); // Button style
            downloadButton.setOnAction(e -> downloadFile(file));  // Function to handle file download

            Label fileNameLabel = new Label(file.getName());
            fileNameLabel.setStyle("-fx-text-fill: #333;"); // Style for file name label

            // Add download button and file name to the file row (button first)
            fileRow.getChildren().addAll(downloadButton, fileNameLabel);
            fileBox.getChildren().add(fileRow);
        }

        // Message on WhatsApp Button
        Button messageButton = new Button("Message on WhatsApp");
        messageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Button style
        messageButton.setOnAction(e -> {
            String url = "https://wa.me/" + user.getWhatsappNumber();
            getHostServices().showDocument(url);
        });

        // Layout with scroll pane
        VBox contentLayout = new VBox(15, nameLabel, emailLabel, whatsappLabel, weightLabel, heightLabel, packageLabel, fileBox, messageButton);
        contentLayout.setPadding(new Insets(20)); // Add padding to the layout
        contentLayout.setAlignment(Pos.CENTER); // Center alignment for the content layout

        ScrollPane scrollPane = new ScrollPane(contentLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F0F0F0;"); // Light background color for the scroll pane

        Scene scene = new Scene(scrollPane, 500, 400);
        detailStage.setScene(scene);
        detailStage.show();
    }

    // Helper method to style labels
    private void styleLabels(Label... labels) {
        for (Label label : labels) {
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;"); // Bold labels with dark text color
        }
    }

    // Function to download a file
    private void downloadFile(File file) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(file.getName()); // Display the original filename including extension
        File saveFile = fileChooser.showSaveDialog(userTable.getScene().getWindow());

        if (saveFile != null) {
            try {
                // Copy the file data to the chosen location
                Files.copy(file.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to get user's files from the database
    private List<File> getUserFiles(int userId) {
        List<File> fileList = new ArrayList<>();
        String sql = "SELECT file_name, file_data FROM user_files WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fileName = rs.getString("file_name");
                byte[] fileData = rs.getBytes("file_data");

                // Write the file data to a temp file
                File tempFile = File.createTempFile("user_" + userId + "_", fileName); // Prefix temp file to avoid conflicts
                Files.write(tempFile.toPath(), fileData);
                fileList.add(tempFile);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return fileList;
    }




    private void updateUser(User user) {
        // Creating a new stage for the update user form
        Stage updateStage = new Stage();
        updateStage.setTitle("Update User");
        updateStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows

        // Form fields with modern design
        TextField fullNameField = new TextField(user.getFullName());
        TextField emailField = new TextField(user.getEmail());
        TextField whatsappField = new TextField(user.getWhatsappNumber());
        TextField weightField = new TextField(String.valueOf(user.getWeight()));
        TextField heightField = new TextField(String.valueOf(user.getHeight()));

        // Package ComboBox
        ComboBox<String> packageComboBox = new ComboBox<>(getPackageNames());
        packageComboBox.setPromptText("Select a Package"); // Prompt text for better user experience

        // File upload button
        Button uploadButton = new Button("Upload Files");
        uploadButton.setPadding(new Insets(8));
        uploadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Green button with white text
        uploadButton.setOnAction(e -> uploadFiles(user.getId())); // Pass user ID to upload function

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setPadding(new Insets(8));
        saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"); // Blue button with white text
        saveButton.setOnAction(e -> {
            // Save user updates to the database
            updateUserInDatabase(user.getId(), fullNameField.getText(), emailField.getText(),
                    whatsappField.getText(), Double.parseDouble(weightField.getText()),
                    Double.parseDouble(heightField.getText()),
                    packageComboBox.getSelectionModel().getSelectedItem());

            updateStage.close();
            refreshUserTable();
        });

        // Layout with modern design
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER); // Center alignment for better aesthetics
        layout.getChildren().addAll(
                new Label("Full Name:"), fullNameField,
                new Label("Email:"), emailField,
                new Label("WhatsApp:"), whatsappField,
                new Label("Weight (kg):"), weightField,
                new Label("Height (cm):"), heightField,
                new Label("Package:"), packageComboBox,
                uploadButton, saveButton
        );

        // Optional: Style the labels
        for (Node node : layout.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;"); // Bold labels with dark text color
            }
        }

        // ScrollPane for better scrolling on small screens
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F0F0F0;"); // Light background color for the scroll pane

        Scene scene = new Scene(scrollPane, 350, 450); // Adjusted scene size for better visibility
        updateStage.setScene(scene);
        updateStage.show();
    }



    private void updateUserInDatabase(int userId, String fullName, String email, String whatsapp, double weight, double height, String packageName) {
        String sql = "UPDATE users SET first_name = ?, email = ?, whatsapp = ?, weight = ?, height = ?, package_id = (SELECT id FROM packages WHERE name = ?) WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, whatsapp);
            pstmt.setDouble(4, weight);
            pstmt.setDouble(5, height);
            pstmt.setString(6, packageName);
            pstmt.setInt(7, userId);
            pstmt.executeUpdate(); // Executes the update query
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void uploadFiles(int userId) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Document Files", "*.doc", "*.pdf", "*.docx", "*.txt", "*.xlsx"));
        List<File> files = fileChooser.showOpenMultipleDialog(userTable.getScene().getWindow());

        if (files != null) {
            for (File file : files) {
                storeFileInDatabase(userId, file);
            }
        }
    }

    private void storeFileInDatabase(int userId, File file) {
        String sql = "INSERT INTO user_files (user_id, file_name, file_type, file_data) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, file.getName());
            pstmt.setString(3, getFileExtension(file));
            pstmt.setBytes(4, Files.readAllBytes(file.toPath())); // Store file data

            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText(user.getFullName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "DELETE FROM users WHERE id = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, user.getId());
                    pstmt.executeUpdate();
                    userList.remove(user);
                    userTable.setItems(userList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void exportUsers() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(userTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (User user : userList) {
                    writer.append(user.getFullName()).append(",")
                            .append(user.getEmail()).append(",")
                            .append(user.getWhatsappNumber()).append(",")
                            .append(String.valueOf(user.getWeight())).append(",")
                            .append(String.valueOf(user.getHeight())).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ObservableList<String> getPackageNames() {
        ObservableList<String> packageNames = FXCollections.observableArrayList();
        String sql = "SELECT name FROM packages";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                packageNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return packageNames;
    }

    private void refreshUserTable() {
        userList.clear();
        userList.addAll(getUsers());
        userTable.setItems(userList);
    }
}
