package views.membership;

public class GymPackage {
    private int id;           // Added ID field
    private String name;
    private int duration;
    private String description;
    private double price;

    public GymPackage(int id, String name, int duration, String description, double price) {
        this.id = id;  // Store the ID
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    // Optionally, if you need to set the ID
    public void setId(int id) {
        this.id = id;
    }
}
