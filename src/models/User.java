package models;

import java.time.LocalDate;

public class User {
    private int id;
    private String firstName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String address;
    private String status;
    private String whatsappNumber;
    private double weight;
    private double height;
    private int packageId;
    private LocalDate joinDate;
    private int age;

    // Constructor
    public User(int id, String firstName, String gender, LocalDate dateOfBirth, String email, String address,
                String status, String whatsappNumber, double weight, double height, int packageId,
                LocalDate joinDate, int age) {
        this.id = id;
        this.firstName = firstName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.address = address;
        this.status = status;
        this.whatsappNumber = whatsappNumber;
        this.weight = weight;
        this.height = height;
        this.packageId = packageId;
        this.joinDate = joinDate;
        this.age = age;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public int getPackageId() {
        return packageId;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public int getAge() {
        return age;
    }

    public String getFullName() {
        return firstName; // Assuming you want to return only the first name as per your table
    }
}
