package youspace.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import youspace.view.user.ProfileUserView;
import youspace.utils.SessionManager;
import youspace.view.LoginView;

import java.util.Optional;

public class SidebarUser extends VBox {

    private final Stage stage;
    private final String activeMenu;

    public SidebarUser(Stage stage, String activeMenu) {

        this.stage = stage;
        this.activeMenu = activeMenu;

        initialize();
    }

    private void initialize() {

        setPrefWidth(220);

        setPadding(new Insets(30));

        setSpacing(14);

        setStyle("-fx-background-color:#183B63;");

        // ================= MENU =================

        Button btnBeranda =      
        createButton("🏠 Beranda", "Beranda");

        btnBeranda.setOnAction(e -> {

            if (!activeMenu.equals("Beranda")) {

                UserDashboardView view =
                new UserDashboardView(stage);

                stage.setScene(
                    view.createScene()
                );
            }
        });

        Button btnVenue =
        createButton("🏢 Venue", "Venue");

        btnVenue.setOnAction(e -> {

            if (!activeMenu.equals("Venue")) {

                VenueUserView view =
                new VenueUserView(stage);

                stage.setScene(
                    view.createScene()
                );
            }
        });

        Button btnFavorite =
        createButton("❤️ Favorit", "Favorit");

        btnFavorite.setOnAction(e -> {

            if (!activeMenu.equals("Favorit")) {

                FavoriteUserView view =
                new FavoriteUserView(stage);

                stage.setScene(
                    view.createScene()
                );
            }
        });

        Button btnBooking =  createButton("📅 Booking", "Booking");

        btnBooking.setOnAction(e -> {

            if (!activeMenu.equals("Booking")) {

                BookingUserView view =
                new BookingUserView(stage);

                stage.setScene(
                    view.createScene()
                );
            }
        });

        Button btnProfile =
        createButton("👤 Profile", "Profile");
    
        btnProfile.setOnAction(e ->
            stage.setScene(
                new ProfileUserView(stage).createScene()
            )
        );

        // ================= ACTION =================

        // BERANDA
        btnBeranda.setOnAction(e -> {

            if (!activeMenu.equals("Beranda")) {

                UserDashboardView view =
                        new UserDashboardView(stage);

                stage.setScene(view.createScene());
            }
        });

        // VENUE
        btnVenue.setOnAction(e -> {

            if (!activeMenu.equals("Venue")) {

                VenueUserView view =
                        new VenueUserView(stage);

                stage.setScene(view.createScene());
            }
        });

        // FAVORITE
        btnFavorite.setOnAction(e -> {
   
            if (!activeMenu.equals("Favorit")) {

                FavoriteUserView view =   
                new FavoriteUserView(stage);

                stage.setScene(
                    view.createScene()
                );
            }
        });

        // ================= SPACER =================

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        // ================= LOGOUT =================

   
        Button btnLogout = createButton("🚪 Log out", "Logout");

  
        btnLogout.setOnAction(e -> {

            Alert alert =
            new Alert(
                Alert.AlertType.CONFIRMATION 
            );
 
            alert.setTitle("Konfirmasi Keluar");
 
            alert.setHeaderText(null);

            alert.setContentText(
                "Apakah anda yakin ingin keluar?" 
            );
 
            Optional<ButtonType> result =
 
            alert.showAndWait();

            if (
                result.isPresent()
                &&
                result.get() == ButtonType.OK 
            ) {

                SessionManager.logout();
                LoginView login = new LoginView(stage);
                stage.setScene(
                    login.createScene()
                );
                stage.centerOnScreen();
            }
        });

        // ================= ADD ALL =================

        getChildren().addAll(
                btnBeranda,
                btnVenue,
                btnFavorite,
                btnBooking,
                btnProfile,
                spacer,
                btnLogout
        );
    }

    private Button createButton(
            String text,
            String menuName
    ) {

        Button btn = new Button(text);

        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setAlignment(Pos.CENTER_LEFT);

        btn.setPadding(
                new Insets(12)
        );

        btn.setFont(
                Font.font(
                        "System",
                        FontWeight.SEMI_BOLD,
                        15
                )
        );

        // ================= ACTIVE MENU =================

        if (menuName.equals(activeMenu)) {

            btn.setStyle("""
                -fx-background-color:#2B6CB0;
                -fx-text-fill:#FFD166;
                -fx-background-radius:8;
                -fx-cursor:hand;
            """);

        } else {

            btn.setStyle("""
                -fx-background-color:transparent;
                -fx-text-fill:white;
                -fx-background-radius:8;
                -fx-cursor:hand;
            """);

            // HOVER EFFECT
            btn.setOnMouseEntered(e -> {

                btn.setStyle("""
                    -fx-background-color:#2D4F77;
                    -fx-text-fill:white;
                    -fx-background-radius:8;
                    -fx-cursor:hand;
                """);
            });

            btn.setOnMouseExited(e -> {

                btn.setStyle("""
                    -fx-background-color:transparent;
                    -fx-text-fill:white;
                    -fx-background-radius:8;
                    -fx-cursor:hand;
                """);
            });
        }

        return btn;
    }
}