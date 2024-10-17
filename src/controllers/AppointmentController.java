package controllers;

import models.Appointment;
import models.AppointmentManager;
import javafx.scene.control.Alert;

public class AppointmentController {

    private AppointmentManager appointmentManager;

    public AppointmentController() {
        this.appointmentManager = new AppointmentManager();

    }


    public void addAppointment(String user, String coach, java.time.LocalDate date,
                               java.time.LocalTime startTime, java.time.LocalTime endTime, String status) {
        if (user.isEmpty() || coach.isEmpty() || date == null || startTime == null || endTime == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill in all fields.");
            return;
        }

        Appointment newAppointment = new Appointment(user, coach, date, startTime, endTime, status);
        appointmentManager.addAppointment(newAppointment);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment added successfully!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
