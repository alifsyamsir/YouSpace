package youspace.view.user;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import youspace.models.AppUser;
import youspace.models.Venue;

import youspace.service.DashboardService;
import youspace.service.VenueService;

import youspace.utils.SessionManager;
import javafx.scene.control.ScrollPane;

public class UserDashboardView {

    private final Stage stage;

    private final DashboardService dashboardService;

    private final VenueService venueService;

    public UserDashboardView(Stage stage) {

        this.stage = stage;

        this.dashboardService =
                new DashboardService();

        this.venueService =
                new VenueService();
    }

    public Scene createScene() {

        AppUser user =
                SessionManager.getCurrentUser();

        BorderPane root =
                new BorderPane();

        root.setStyle("""
            -fx-background-color:#F8FAFC;
        """);

        root.setLeft(
                new SidebarUser(stage, "Beranda")
        );

        VBox content =
                new VBox(28);

        content.setPadding(
                new Insets(35)
        );

        // HEADER

        HBox topBar =
                new HBox();

        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox headerText =
                new VBox(6);

        Label title =
                new Label("Beranda");

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        42
                )
        );

        title.setStyle("""
            -fx-text-fill:#183B63;
        """);

        Label subtitle =
                new Label(
                        "Halo, "
                        + user.getName()
                        + " 👋"
                );

        subtitle.setStyle("""
            -fx-text-fill:#64748B;
            -fx-font-size:15;
        """);

        headerText.getChildren().addAll(
                title,
                subtitle
        );

        topBar.getChildren().add(headerText);

        // STAT CARD

        HBox statRow =
                new HBox(22);

        VBox venueCard =
                createStatCard(
                        String.valueOf(
                                dashboardService
                                .getAvailableVenueForUser()
                        ),
                        "Venue tersedia"
                );

        VBox bookingCard =
                createStatCard(
                        String.valueOf(
                                dashboardService
                                .getActiveBookingByUser(
                                        user.getId()
                                )
                        ),
                        "Booking Aktif"
                );

        VBox wishlistCard =
                createStatCard(
                        String.valueOf(
                                dashboardService
                                .getWishlistByUser(
                                        user.getId()
                                )
                        ),
                        "Venue disimpan"
                );

        statRow.getChildren().addAll(
                venueCard,
                bookingCard,
                wishlistCard
        );

        // RECOMMENDED

        Label recommendTitle =
                new Label(
                        "Recommended Venues"
                );

        recommendTitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        26
                )
        );

        recommendTitle.setStyle("""
            -fx-text-fill:#183B63;
        """);

        VBox recommendContainer =
                new VBox(18);

        List<Venue> venues =
                venueService.getRecommendedVenues();

        for (Venue venue : venues) {

            HBox card =
                    createRecommendedCard(venue);

            recommendContainer
                    .getChildren()
                    .add(card);
        }

        content.getChildren().addAll(
                topBar,
                statRow,
                recommendTitle,
                recommendContainer
        );

        ScrollPane scrollPane =
        new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle("""
                -fx-background:#F8FAFC;
                -fx-background-color:#F8FAFC;
        """);

        root.setCenter(scrollPane);

        return new Scene(root, 1280, 760);
    }

    private VBox createStatCard(
            String value,
            String title
    ) {

        VBox card =
                new VBox(10);

        card.setPrefWidth(170);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle("""
            -fx-background-color:#183B63;
            -fx-background-radius:22;
        """);

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle("""
            -fx-text-fill:#FFF7D6;
        """);

        valueLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        50
                )
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:18;
        """);

        card.getChildren().addAll(
                valueLabel,
                titleLabel
        );

        return card;
    }

    private HBox createRecommendedCard(
            Venue venue
    ) {

        HBox card =
                new HBox(20);

        card.setPadding(
                new Insets(18)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:22;
            -fx-cursor:hand;
        """);

        ImageView imageView =
                new ImageView();

        try {

            imageView.setImage(
                    new Image(
                            new File(
                                    venue.getImagePath()
                            ).toURI().toString()
                    )
            );

        } catch (Exception e) {

            System.out.println(
                    "Gagal load gambar"
            );
        }

        imageView.setFitWidth(250);

        imageView.setFitHeight(180);

        imageView.setPreserveRatio(false);

        VBox body =
                new VBox(12);

        Label name =
                new Label(
                        venue.getName()
                );

        name.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        30
                )
        );

        Label category =
                new Label(
                        formatCategory(
                                venue.getCategory().name()
                        )
                );

        category.setStyle("""
            -fx-background-color:#D9EAF7;
            -fx-padding:6 12;
            -fx-background-radius:8;
        """);

        Label desc =
                new Label(
                        truncateText(
                                venue.getDescription(),
                                160
                        )
                );

        desc.setWrapText(true);

        desc.setStyle("""
            -fx-text-fill:#475569;
        """);

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        String price =
                rupiah.format(
                        venue.getPricePerDay()
                ).replace(",00", "");

        Label lblPrice =
                new Label(
                        price + "/hari"
                );

        lblPrice.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        22
                )
        );

        Button btn =
                new Button(
                        "Pesan Sekarang →"
                );

        btn.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-background-radius:10;
            -fx-padding:10 18;
            -fx-cursor:hand;
        """);

        btn.setOnAction(e -> {

            DetailVenueView detail =
                    new DetailVenueView(
                            stage,
                            venue
                    );

            stage.setScene(
                    detail.createScene()
            );
        });

        body.getChildren().addAll(
                name,
                category,
                desc,
                lblPrice,
                btn
        );

        card.getChildren().addAll(
                imageView,
                body
        );

        card.setOnMouseClicked(e -> {

            DetailVenueView detail =
                    new DetailVenueView(
                            stage,
                            venue
                    );

            stage.setScene(
                    detail.createScene()
            );
        });

        return card;
    }

    private String truncateText(
            String text,
            int max
    ) {

        if (text == null) {
            return "";
        }

        if (text.length() <= max) {
            return text;
        }

        return text.substring(0, max) + "...";
    }

    private String formatCategory(String value) {

        return switch (value) {

            case "MEETING_ROOM" ->
                    "Meeting Room";

            case "WEDDING_HALL" ->
                    "Wedding Hall";

            default ->
                    value.substring(0, 1)
                    + value.substring(1).toLowerCase();
        };
    }
}