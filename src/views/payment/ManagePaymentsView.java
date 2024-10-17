package views.payment;

import controllers.PaymentController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Payment;

public class ManagePaymentsView extends Application {

    private TextField searchField = new TextField();
    private Label fullNameLabel = new Label();
    private Label packageLabel = new Label();
    private Label validDateLabel = new Label();
    private Label priceLabel = new Label();
    private Button confirmButton = new Button("Confirm Payment");
    private Button printButton = new Button("Print Receipt");

    private PaymentController paymentController = new PaymentController();
    private Payment currentPayment; // To store the currently loaded payment details

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Payment Management");

        // Layout setup
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // Search field setup
        searchField.setPromptText("Search by Name");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchUser(newValue));

        // Confirm button action
        confirmButton.setOnAction(e -> confirmPayment());
        printButton.setOnAction(e -> printReceipt());

        // Layout
        layout.setTop(searchField);
        layout.setCenter(createPaymentDetailsPane());
        layout.setBottom(createButtonPane());

        Scene scene = new Scene(layout, 400, 300); // Adjusted height for UI
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createPaymentDetailsPane() {
        GridPane paymentDetailsPane = new GridPane();
        paymentDetailsPane.setPadding(new Insets(10));
        paymentDetailsPane.setVgap(10);
        paymentDetailsPane.setHgap(10);

        paymentDetailsPane.add(new Label("Full Name:"), 0, 0);
        paymentDetailsPane.add(fullNameLabel, 1, 0);
        paymentDetailsPane.add(new Label("Package:"), 0, 1);
        paymentDetailsPane.add(packageLabel, 1, 1);
        paymentDetailsPane.add(new Label("Valid Date:"), 0, 2);
        paymentDetailsPane.add(validDateLabel, 1, 2);
        paymentDetailsPane.add(new Label("Price:"), 0, 3);
        paymentDetailsPane.add(priceLabel, 1, 3);

        return paymentDetailsPane;
    }

    private HBox createButtonPane() {
        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10));
        buttonPane.getChildren().addAll(confirmButton, printButton);
        return buttonPane;
    }

    private void searchUser(String name) {
        try {
            currentPayment = paymentController.searchUser(name);
            if (currentPayment != null) {
                // Populate the labels with the fetched details
                fullNameLabel.setText(currentPayment.getFirstName());
                packageLabel.setText(currentPayment.getPackageName());
                validDateLabel.setText(currentPayment.getValidDate().toString());
                priceLabel.setText(String.valueOf(currentPayment.getPrice()));
            } else {
                // No user found
                clearFields();
                Alert alert = new Alert(Alert.AlertType.WARNING, "No user found with that name!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error occurred while searching for user: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void clearFields() {
        fullNameLabel.setText("");
        packageLabel.setText("");
        validDateLabel.setText("");
        priceLabel.setText("");
    }

    private void confirmPayment() {
        if (currentPayment != null) {
            paymentController.confirmPayment(currentPayment);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment confirmed!", ButtonType.OK);
            alert.showAndWait();
            clearFields();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please search for a user before confirming payment.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void printReceipt() {
        if (currentPayment != null) {
            paymentController.printReceipt(currentPayment);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Receipt printed to console!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please search for a user before printing a receipt.", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
