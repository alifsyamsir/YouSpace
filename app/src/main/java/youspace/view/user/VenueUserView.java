package youspace.view.user;

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

import youspace.enums.VenueCategory;

import youspace.models.Venue;

import youspace.service.FavoriteService;
import youspace.service.VenueService;

import youspace.utils.SessionManager;
import youspace.utils.ViewUtil;

import java.io.File;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;

public class VenueUserView {

    private final Stage stage;

    private final VenueService venueService;

    private final FavoriteService favoriteService;

    private GridPane venueGrid;

    private String currentCategory = "Semua";

    public VenueUserView(Stage stage) {

        this.stage = stage;

        this.venueService =
                new VenueService();

        this.favoriteService =
                new FavoriteService();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle("""
            -fx-background-color:#F8FAFC;
        """);

        root.setLeft(
                new SidebarUser(stage, "Venue")
        );

        VBox mainContent =
                new VBox(25);

        mainContent.setPadding(
                new Insets(35)
        );

        Label title =
                new Label("Venue");

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

        // FILTER

        HBox filterBar =
                new HBox(12);

        String[] categories = {
                "Semua",
                "Aula",
                "Ballroom",
                "Meeting Room",
                "Studio",
                "Wedding Hall"
        };

        for (String category : categories) {

            Button btn =
                    new Button(category);

            styleFilterButton(
                    btn,
                    category.equals(currentCategory)
            );

            btn.setOnAction(e -> {

                currentCategory = category;

                filterBar.getChildren().forEach(node -> {

                    Button b = (Button) node;

                    styleFilterButton(
                            b,
                            b.getText().equals(currentCategory)
                    );
                });

                loadVenues();
            });

            filterBar.getChildren().add(btn);
        }

        venueGrid =
                new GridPane();

        venueGrid.setHgap(28);

        venueGrid.setVgap(28);

        ScrollPane scroll =
                new ScrollPane(venueGrid);

        scroll.setFitToWidth(true);

        scroll.setStyle("""
            -fx-background-color:transparent;
            -fx-background:transparent;
        """);

        loadVenues();

        mainContent.getChildren().addAll(
                title,
                filterBar,
                scroll
        );

        root.setCenter(  
                ViewUtil.createScrollable(mainContent)
        );

        return new Scene(root, 1280, 760);
    }

    private void loadVenues() {

        venueGrid.getChildren().clear();

        List<Venue> venues;

        if (currentCategory.equals("Semua")) {

            venues =
                    venueService.getAllVenues();

        } else {

            String enumName =
                    currentCategory
                            .toUpperCase()
                            .replace(" ", "_");

            VenueCategory category =
                    VenueCategory.valueOf(enumName);

            venues =
                    venueService.getVenuesByCategory(category);
        }

        int col = 0;
        int row = 0;

        for (Venue venue : venues) {

            VBox card =
                    createVenueCard(venue);

            venueGrid.add(card, col, row);

            col++;

            if (col == 2) {

                col = 0;
                row++;
            }
        }
    }

    public VBox createVenueCard(Venue venue) {

        VBox card =
                new VBox(14);

        card.setPrefWidth(370);

        card.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:20;
            -fx-border-radius:20;
            -fx-border-color:#E5E7EB;
            -fx-cursor:hand;
        """);

        // IMAGE

        StackPane imageContainer =
                new StackPane();

        ImageView imageView =
                new ImageView();

        try {

            Image image =
                    new Image(
                            new File(
                                    venue.getImagePath()
                            ).toURI().toString()
                    );

            imageView.setImage(image);

        } catch (Exception e) {

            System.out.println("Gagal load gambar");
        }

        imageView.setFitWidth(370);

        imageView.setFitHeight(220);

        imageView.setPreserveRatio(false);

        Button bookmarkBtn =
                new Button();

        int userId =
                SessionManager
                        .getCurrentUser()
                        .getId();

        boolean isFav =
                favoriteService.isFavorite(
                        userId,
                        venue.getId()
                );

        bookmarkBtn.setText(
                isFav ? "★" : "☆"
        );

        bookmarkBtn.setStyle("""
            -fx-background-color:rgba(255,255,255,0.9);
            -fx-text-fill:#F59E0B;
            -fx-font-size:22;
            -fx-background-radius:50;
            -fx-cursor:hand;
        """);

        bookmarkBtn.setOnAction(e -> {

            boolean current =
                    favoriteService.isFavorite(
                            userId,
                            venue.getId()
                    );

            if (current) {

                favoriteService.removeFavorite(
                        userId,
                        venue.getId()
                );

                bookmarkBtn.setText("☆");

            } else {

                favoriteService.addFavorite(
                        userId,
                        venue.getId()
                );

                bookmarkBtn.setText("★");
            }

            e.consume();
        });

        imageContainer.getChildren().addAll(
                imageView,
                bookmarkBtn
        );

        StackPane.setAlignment(
                bookmarkBtn,
                Pos.TOP_RIGHT
        );

        StackPane.setMargin(
                bookmarkBtn,
                new Insets(14)
        );

        // CONTENT

        VBox body =
                new VBox(12);

        body.setPadding(
                new Insets(0, 18, 18, 18)
        );

        Label name =
                new Label(
                        venue.getName()
                );

        name.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        20
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
            -fx-background-radius:8;
            -fx-padding:5 14;
            -fx-font-size:11;
            -fx-font-weight:bold;
        """);

        Label desc =
                new Label(
                        truncateText(
                                venue.getDescription(),
                                120
                        )
                );

        desc.setWrapText(true);

        desc.setStyle("""
            -fx-text-fill:#4B5563;
            -fx-font-size:12;
        """);

        // FOOTER

        HBox footer =
                new HBox();

        footer.setAlignment(
                Pos.CENTER_LEFT
        );

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
                        18
                )
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button btnBooking =
                new Button(
                        "Pesan Sekarang →"
                );

        btnBooking.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-background-radius:10;
            -fx-padding:10 18;
            -fx-cursor:hand;
        """);

        btnBooking.setOnAction(e -> {

            DetailVenueView detail =
                    new DetailVenueView(
                            stage,
                            venue
                    );

            stage.setScene(
                    detail.createScene()
            );

            e.consume();
        });

        footer.getChildren().addAll(
                lblPrice,
                spacer,
                btnBooking
        );

        body.getChildren().addAll(
                name,
                category,
                desc,
                footer
        );

        card.getChildren().addAll(
                imageContainer,
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

    private void styleFilterButton(
            Button btn,
            boolean active
    ) {

        if (active) {

            btn.setStyle("""
                -fx-background-color:#183B63;
                -fx-text-fill:white;
                -fx-background-radius:12;
                -fx-padding:10 24;
                -fx-font-weight:bold;
            """);

        } else {

            btn.setStyle("""
                -fx-background-color:#D9EAF7;
                -fx-text-fill:#183B63;
                -fx-background-radius:12;
                -fx-padding:10 24;
                -fx-font-weight:bold;
                -fx-cursor:hand;
            """);
        }
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