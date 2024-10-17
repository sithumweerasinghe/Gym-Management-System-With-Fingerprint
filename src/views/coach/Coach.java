package views.coach;

public class Coach {
    private String firstName;
    private String lastName;
    private String username;
    private String timePeriod;
    private int customDuration;

    public Coach(String firstName, String lastName, String username, String timePeriod, int customDuration) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.timePeriod = timePeriod;
        this.customDuration = customDuration;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public int getCustomDuration() {
        return customDuration;
    }
}
