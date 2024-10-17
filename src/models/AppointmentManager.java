package models;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {

    /**
     * Adds a new appointment to the database.
     *
     * @param appointment the Appointment object to be added.
     */
    public void addAppointment(Appointment appointment) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO appointments (user, coach, appointment_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, appointment.getUser());
            stmt.setString(2, appointment.getCoach());
            stmt.setDate(3, java.sql.Date.valueOf(appointment.getDate()));
            stmt.setTime(4, java.sql.Time.valueOf(appointment.getStartTime()));
            stmt.setTime(5, java.sql.Time.valueOf(appointment.getEndTime()));
            stmt.setString(6, appointment.getStatus());
            stmt.executeUpdate();
            System.out.println("Appointment added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches a list of all appointments from the database.
     *
     * @return a List of Appointment objects.
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM appointments";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String user = rs.getString("user");
                String coach = rs.getString("coach");
                java.sql.Date appointmentDate = rs.getDate("appointment_date");
                java.sql.Time startTime = rs.getTime("start_time");
                java.sql.Time endTime = rs.getTime("end_time");
                String status = rs.getString("status");

                Appointment appointment = new Appointment(user, coach, appointmentDate.toLocalDate(),
                        startTime.toLocalTime(), endTime.toLocalTime(), status);
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}
