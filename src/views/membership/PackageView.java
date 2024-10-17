package views.membership;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageView extends Application {

    private TableView<GymPackage> tableView = new TableView<>();
    private TextField packageNameField, packageDurationField, packageDescriptionField, packagePriceField;
    private TextField searchField;
    private List<GymPackage> packagesList = new ArrayList<>();
    private GymPackage selectedPackage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Package Management");

        // Create the layout
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Create the table view
        createPackageTable();
        loadPackages();

        layout.setCenter(tableView);

        // Create input form
        createInputForm(layout);

        // Create search bar
        createSearchBar(layout);

        // Set the scene
        Scene scene = new Scene(layout, 800, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Add your CSS
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createSearchBar(BorderPane layout) {
        searchField = new TextField();
        searchField.setPromptText("Search Packages...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchPackages(newValue));

        HBox searchBox = new HBox(10, searchField);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        layout.setTop(searchBox);
    }

    private void searchPackages(String query) {
        List<GymPackage> filteredPackages = new ArrayList<>();
        for (GymPackage pkg : packagesList) {
            if (pkg.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredPackages.add(pkg);
            }
        }
        tableView.getItems().setAll(filteredPackages);
    }

    private void createPackageTable() {
        TableColumn<GymPackage, String> nameColumn = new TableColumn<>("Package Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setSortable(true); // Enable sorting

        TableColumn<GymPackage, Integer> durationColumn = new TableColumn<>("Duration (days)");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.setSortable(true); // Enable sorting

        TableColumn<GymPackage, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<GymPackage, Double> priceColumn = new TableColumn<>("Price (LKR)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setSortable(true); // Enable sorting

        TableColumn<GymPackage, String> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<GymPackage, String>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10, updateButton, deleteButton);
                    setGraphic(hBox);

                    // Styling buttons
                    updateButton.setStyle("-fx-padding: 5;");
                    deleteButton.setStyle("-fx-padding: 5; -fx-background-color: red; -fx-text-fill: white;");

                    updateButton.setOnAction(e -> openUpdatePackageWindow(getTableRow().getItem()));
                    deleteButton.setOnAction(e -> deletePackage(getTableRow().getItem()));
                }
            }
        });

        tableView.getColumns().addAll(nameColumn, durationColumn, descriptionColumn, priceColumn, actionsColumn);
    }

    private void createInputForm(BorderPane layout) {
        packageNameField = new TextField();
        packageDurationField = new TextField();
        packageDescriptionField = new TextField();
        packagePriceField = new TextField();

        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setVgap(10);
        form.setHgap(10);
        form.add(new Label("Package Name:"), 0, 0);
        form.add(packageNameField, 1, 0);
        form.add(new Label("Duration (days):"), 0, 1);
        form.add(packageDurationField, 1, 1);
        form.add(new Label("Description:"), 0, 2);
        form.add(packageDescriptionField, 1, 2);
        form.add(new Label("Price ($):"), 0, 3);
        form.add(packagePriceField, 1, 3);

        Button addPackageButton = new Button("Add Package");
        addPackageButton.setOnAction(e -> addPackage());
        form.add(addPackageButton, 1, 4);

        layout.setBottom(form);
    }

    private void loadPackages() {
        packagesList.clear(); // Clear previous data
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM packages")) {

            while (rs.next()) {
                GymPackage pkg = new GymPackage(
                        rs.getInt("id"), // Adding ID
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("description"),
                        rs.getDouble("price")
                );
                packagesList.add(pkg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load packages.");
        }

        tableView.getItems().setAll(packagesList);
    }

    private void addPackage() {
        String name = packageNameField.getText();
        String duration = packageDurationField.getText();
        String description = packageDescriptionField.getText();
        String price = packagePriceField.getText();

        if (validateInput(name, duration, price)) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO packages (name, duration, description, price) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, name);
                statement.setInt(2, Integer.parseInt(duration));
                statement.setString(3, description);
                statement.setDouble(4, Double.parseDouble(price));
                statement.executeUpdate();
                showAlert("Success", "Package added successfully!");
                loadPackages();
                clearInputFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Error adding package to database.");
            }
        }
    }

    private void openUpdatePackageWindow(GymPackage pkg) {
        selectedPackage = pkg;

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Package");
        updateStage.initModality(Modality.APPLICATION_MODAL);

        GridPane updateForm = new GridPane();
        updateForm.setPadding(new Insets(20));
        updateForm.setVgap(15);
        updateForm.setHgap(10);
        updateForm.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(pkg.getName());
        TextField durationField = new TextField(String.valueOf(pkg.getDuration()));
        TextField descriptionField = new TextField(pkg.getDescription());
        TextField priceField = new TextField(String.valueOf(pkg.getPrice()));

        updateForm.add(new Label("Package Name:"), 0, 0);
        updateForm.add(nameField, 1, 0);
        updateForm.add(new Label("Duration (days):"), 0, 1);
        updateForm.add(durationField, 1, 1);
        updateForm.add(new Label("Description:"), 0, 2);
        updateForm.add(descriptionField, 1, 2);
        updateForm.add(new Label("Price ($):"), 0, 3);
        updateForm.add(priceField, 1, 3);

        Button updateButton = new Button("Update Package");
        updateButton.setOnAction(e -> {
            String newName = nameField.getText();
            String newDuration = durationField.getText();
            String newDescription = descriptionField.getText();
            String newPrice = priceField.getText();

            if (validateInput(newName, newDuration, newPrice)) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE packages SET name = ?, duration = ?, description = ?, price = ? WHERE id = ?";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, newName);
                    statement.setInt(2, Integer.parseInt(newDuration));
                    statement.setString(3, newDescription);
                    statement.setDouble(4, Double.parseDouble(newPrice));
                    statement.setInt(5, pkg.getId());
                    statement.executeUpdate();
                    showAlert("Success", "Package updated successfully!");
                    loadPackages();
                    updateStage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Error updating package in database.");
                }
            }
        });
        updateForm.add(updateButton, 1, 4);

        Scene scene = new Scene(updateForm, 400, 300);
        updateStage.setScene(scene);
        updateStage.show();
    }

    private void deletePackage(GymPackage pkg) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM packages WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, pkg.getId());
            statement.executeUpdate();
            showAlert("Success", "Package deleted successfully!");
            loadPackages();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error deleting package.");
        }
    }

    private boolean validateInput(String name, String duration, String price) {
        if (name.isEmpty() || duration.isEmpty() || price.isEmpty()) {
            showAlert("Validation Error", "All fields are required!");
            return false;
        }
        try {
            Integer.parseInt(duration);
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid duration or price format!");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        packageNameField.clear();
        packageDurationField.clear();
        packageDescriptionField.clear();
        packagePriceField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
