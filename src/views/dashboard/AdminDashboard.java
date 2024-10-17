package views.dashboard;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import views.appointment.AppointmentAddView;
import views.coach.AddCoachView;
import views.membership.PackageView;
import views.payment.ManagePaymentsView;
import views.user.AddUserView;
import views.user.ManageUsersView;

public class AdminDashboard extends Application {

    private static final Color PRIMARY_COLOR = Color.rgb(95, 43, 214); // #5f2bd6
    private static final Color SECONDARY_COLOR = Color.rgb(152, 56, 252); // #9838fc
    private static final Color BACKGROUND_COLOR = Color.rgb(21, 21, 21); // #151515
    private static final Color CARD_COLOR = Color.rgb(44, 44, 44); // #2C2C2C
    private static final Color WHITE_COLOR = Color.WHITE; // #FFFFFF
    private VBox currentNavItem = null; // To track the current highlighted nav item

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Dashboard");

        // Main layout with scrollable content
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #151515;");

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Add left navigation bar
        VBox leftNavBar = createLeftNavBar(primaryStage);
        mainLayout.setLeft(leftNavBar);

        // Add top header
        VBox topHeader = createTopHeader();
        mainLayout.setTop(topHeader);

        // Create feature cards
        GridPane cardGrid = createCardGrid(primaryStage);
        mainLayout.setCenter(cardGrid);

        // Set scene
        Scene scene = new Scene(scrollPane, 1000, 680); // Increased size for modern look
        scene.getStylesheets().add("styles.css"); // Add external CSS file for styling
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create the navigation bar
    private VBox createLeftNavBar(Stage primaryStage) {
        VBox navigationBar = new VBox(15); // Spacing between elements
        navigationBar.setPadding(new Insets(30, 10, 20, 20)); // Padding for aesthetic look
        navigationBar.setStyle("-fx-background-color: #2C2C2C;"); // Darker background for contrast
        navigationBar.setPrefWidth(220); // Wider navigation bar

        navigationBar.getChildren().add(createNavButton("Add User", primaryStage));
        navigationBar.getChildren().add(createNavButton("Add Coach", primaryStage));
        navigationBar.getChildren().add(createNavButton("Add Packages", primaryStage));
        navigationBar.getChildren().add(createNavButton("Manage Users", primaryStage));
        navigationBar.getChildren().add(createNavButton("Manage Payments", primaryStage));
        navigationBar.getChildren().add(createNavButton("Get Reports", primaryStage));
        navigationBar.getChildren().add(createNavButton("Manage Appointment", primaryStage));
        navigationBar.getChildren().add(createNavButton("Profile Settings", primaryStage));

        Button logoutButton = createLogoutButton(primaryStage);
        navigationBar.getChildren().add(logoutButton);

        return navigationBar;
    }

    // Create the top header
    private VBox createTopHeader() {
        Label welcomeLabel = new Label("Welcome to the Admin Dashboard");
        welcomeLabel.setFont(new Font("Roboto", 28));
        welcomeLabel.setTextFill(WHITE_COLOR);
        welcomeLabel.setPadding(new Insets(25, 10, 25, 10));

        VBox topSection = new VBox(welcomeLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setStyle("-fx-background-color: #2C2C2C;");
        return topSection;
    }

    // Create the grid for feature cards
    private GridPane createCardGrid(Stage primaryStage) {
        GridPane cardGrid = new GridPane();
        cardGrid.setPadding(new Insets(30)); // Padding around grid
        cardGrid.setHgap(20); // Horizontal gap between cards
        cardGrid.setVgap(20); // Vertical gap between cards
        cardGrid.setAlignment(Pos.TOP_CENTER);  // Align cards to the top center

        cardGrid.add(createFeatureCard("Add User", "https://img.icons8.com/fluent/48/add-user-male.png", primaryStage), 0, 0);
        cardGrid.add(createFeatureCard("Add Coach", "https://img.icons8.com/fluent/48/trainer.png", primaryStage), 1, 0);
        cardGrid.add(createFeatureCard("Add Packages", "https://img.icons8.com/fluent/48/box.png", primaryStage), 2, 0);
        cardGrid.add(createFeatureCard("Manage Users", "https://img.icons8.com/fluent/48/group.png", primaryStage), 0, 1);
        cardGrid.add(createFeatureCard("Manage Payments", "https://img.icons8.com/fluent/48/money.png", primaryStage), 1, 1);
        cardGrid.add(createFeatureCard("Attendant", "https://img.icons8.com/fluent/48/attendance-mark.png", primaryStage), 2, 1);

        return cardGrid;
    }

    // Create a feature card with modern hover effects and transitions
    private VBox createFeatureCard(String text, String iconUrl, Stage primaryStage) {
        VBox card = new VBox(10); // 10px spacing between icon and label
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(150, 150);
        card.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 5);"); // Add shadow for depth

        // Icon
        Image iconImage = new Image(iconUrl);
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(48);
        iconView.setFitHeight(48);

        // Label
        Label label = new Label(text);
        label.setTextFill(WHITE_COLOR);
        label.setFont(new Font("Roboto", 16));

        // Add icon and label to card
        card.getChildren().addAll(iconView, label);

        // Hover effect: scale transition
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        card.setOnMouseEntered(e -> {
            st.setToX(1.1);
            st.setToY(1.1);
            st.playFromStart();
        });
        card.setOnMouseExited(e -> {
            st.setToX(1);
            st.setToY(1);
            st.playFromStart();
        });

        return card;
    }

    // Create a styled logout button
    private Button createLogoutButton(Stage primaryStage) {
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #9838fc; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
        logoutButton.setOnMouseEntered(event -> logoutButton.setStyle("-fx-background-color: #5f2bd6; -fx-text-fill: white;"));
        logoutButton.setOnMouseExited(event -> logoutButton.setStyle("-fx-background-color: #9838fc; -fx-text-fill: white;"));

        logoutButton.setOnAction(event -> {
            // Handle logout logic here
            primaryStage.close();
        });

        return logoutButton;
    }

    // Create navigation buttons with hover effects
    private VBox createNavButton(String text, Stage primaryStage) {
        VBox navButton = new VBox();
        navButton.setAlignment(Pos.CENTER_LEFT);
        navButton.setPadding(new Insets(10));
        navButton.setStyle("-fx-background-color: #2C2C2C; -fx-border-radius: 10;");

        Label label = new Label(text);
        label.setTextFill(WHITE_COLOR);
        label.setFont(new Font("Roboto", 14));

        navButton.setOnMouseEntered(event -> navButton.setStyle("-fx-background-color: #5f2bd6; -fx-border-radius: 10;"));
        navButton.setOnMouseExited(event -> {
            if (navButton != currentNavItem) {
                navButton.setStyle("-fx-background-color: #2C2C2C; -fx-border-radius: 10;");
            }
        });

        navButton.setOnMouseClicked(event -> {
            if (currentNavItem != null) {
                currentNavItem.setStyle("-fx-background-color: #2C2C2C; -fx-border-radius: 10;");
            }
            currentNavItem = navButton;
            navButton.setStyle("-fx-background-color: #5f2bd6; -fx-border-radius: 10;");

            // Logic for switching views based on the clicked button
            switch (text) {
                case "Add User":
                    new AddUserView().start(new Stage());
                    break;
                case "Add Coach":
                    new AddCoachView().start(new Stage());
                    break;
                case "Add Packages":
                    new PackageView().start(new Stage());
                    break;
                case "Manage Users":
                    new ManageUsersView().start(new Stage());
                    break;
                case "Manage Payments":
                    new ManagePaymentsView().start(new Stage());
                    break;
                case "Profile Settings":
                    new ProfileSettingsView().start(new Stage());
                    break;
                case "Manage Appointment":
                    new AppointmentAddView().start(new Stage());
                    break;
                default:
                    System.out.println(text + " clicked!");
                    break;
            }
        });

        navButton.getChildren().add(label);
        return navButton;
    }
}
