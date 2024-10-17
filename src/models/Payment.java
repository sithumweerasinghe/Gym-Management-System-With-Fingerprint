package models;

import java.time.LocalDate;

public class Payment {
    private String firstName; // Renamed from fullName to firstName
    private String coach; // This will be set to null when fetching payments
    private String packageName;
    private LocalDate validDate;
    private double price;

    // Updated constructor to reflect firstName
    public Payment(String firstName, String coach, String packageName, LocalDate validDate, double price) {
        this.firstName = firstName; // Use firstName instead of fullName
        this.coach = coach; // This will be null
        this.packageName = packageName;
        this.validDate = validDate;
        this.price = price;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
