package views.appointment;

import controllers.AppointmentController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.AppointmentManager;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentAddView extends Application {

    private AppointmentController appointmentController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        appointmentController = new AppointmentController();

        primaryStage.setTitle("Add New Appointment");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);

        // User Name Field
        Label userLabel = new Label("User:");
        TextField userField = new TextField();
        userField.setPromptText("Enter User Name");
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);

        // Coach Name Field
        Label coachLabel = new Label("Coach:");
        ComboBox<String> coachComboBox = new ComboBox<>();
        loadCoaches(coachComboBox);
        grid.add(coachLabel, 0, 1);
        grid.add(coachComboBox, 1, 1);

        // Appointment Date
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        grid.add(dateLabel, 0, 2);
        grid.add(datePicker, 1, 2);

        // Appointment Start Time
        Label startTimeLabel = new Label("Start Time:");
        ComboBox<LocalTime> startTimeComboBox = new ComboBox<>();
        startTimeComboBox.getItems().addAll(
                LocalTime.of(6, 0), LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0),
                LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0), LocalTime.of(13, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)
        );
        startTimeComboBox.setValue(LocalTime.now());
        grid.add(startTimeLabel, 0, 3);
        grid.add(startTimeComboBox, 1, 3);

        // Appointment End Time
        Label endTimeLabel = new Label("End Time:");
        ComboBox<LocalTime> endTimeComboBox = new ComboBox<>();
        endTimeComboBox.getItems().addAll(
                LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(11, 0), LocalTime.of(12, 0), LocalTime.of(13, 0), LocalTime.of(14, 0),
                LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0), LocalTime.of(18, 0)
        );
        endTimeComboBox.setValue(LocalTime.now().plusHours(1));
        grid.add(endTimeLabel, 0, 4);
        grid.add(endTimeComboBox, 1, 4);

        // Status (can be "Scheduled", "Completed")
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Scheduled", "Completed");
        statusComboBox.setValue("Scheduled");
        grid.add(statusLabel, 0, 5);
        grid.add(statusComboBox, 1, 5);

        // Add Appointment Button
        Button addButton = new Button("Add Appointment");
        addButton.setOnAction(e -> {
            String user = userField.getText();
            String coach = coachComboBox.getValue();
            LocalDate date = datePicker.getValue();
            LocalTime startTime = startTimeComboBox.getValue();
            LocalTime endTime = endTimeComboBox.getValue();
            String status = statusComboBox.getValue();
            appointmentController.addAppointment(user, coach, date, startTime, endTime, status);
        });
        grid.add(addButton, 0, 6, 2, 1);

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadCoaches(ComboBox<String> coachComboBox) {
        Class<? extends AppointmentManager> coaches = new AppointmentManager().getClass();
        coachComboBox.getItems().addAll(String.valueOf(coaches));
    }
}
