package youspace.view.user;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import youspace.models.Venue;
import youspace.service.FavoriteService;
import youspace.utils.ViewUtil;

public class DetailVenueView {

    private final Stage stage;
    private final Venue venue;

    private final FavoriteService favoriteService;

    public DetailVenueView(Stage stage, Venue venue) {

        this.stage = stage;
        this.venue = venue;

        this.favoriteService =
                new FavoriteService();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color:#F8F9FA;");

        root.setLeft(
                new SidebarUser(stage, "Venue")
        );

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(35)
        );

        Label title = new Label(
                "Detail Venue"
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        30
                )
        );

        ImageView imageView =
                new ImageView();

        try {

            Image image = new Image(
                    new File(
                            venue.getImagePath()
                    ).toURI().toString()
            );

            imageView.setImage(image);

        } catch (Exception e) {

            System.out.println(
                    "Gagal load gambar"
            );
        }

        imageView.setFitWidth(700);

        imageView.setFitHeight(320);

        imageView.setPreserveRatio(false);

        VBox card = new VBox(18);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:15;
        """);

        Label name = new Label(
                venue.getName()
        );

        name.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        28
                )
        );

        Label category = new Label(
                venue.getCategory().name()
        );

        category.setStyle("""
            -fx-background-color:#E6F0FF;
            -fx-padding:6 14;
            -fx-background-radius:8;
        """);

        Label capacity = new Label(
                "Kapasitas : "
                + venue.getCapacity()
                + " orang"
        );

        Label price = new Label(
                "Rp "
                + String.format(
                    "%,.0f",
                    venue.getPricePerDay()
                )
                + " / hari"
        );

        price.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        24
                )
        );

        TextArea desc =
                new TextArea(
                        venue.getDescription()
                );

        desc.setWrapText(true);

        desc.setEditable(false);

        desc.setPrefHeight(120);

        Button favoriteBtn =
                new Button("♡ Simpan Venue");

        favoriteBtn.setStyle("""
            -fx-background-color:#EDF2F7;
            -fx-font-weight:bold;
        """);

        favoriteBtn.setOnAction(e -> {

            favoriteService.addFavorite(
                    youspace.utils.SessionManager
                            .getCurrentUser()
                            .getId(),

                    venue.getId()
            );

            favoriteBtn.setText(
                    "✓ Tersimpan"
            );
        });

        Button bookingBtn =
                new Button("Pesan Sekarang");

        bookingBtn.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-padding:12 30;
        """);

        bookingBtn.setOnAction(e -> {

            BookingVenueView bookingView =
                    new BookingVenueView(
                            stage,
                            venue
                    );

            stage.setScene(
                    bookingView.createScene()
            );
        });

        HBox actions =
                new HBox(
                        15,
                        favoriteBtn,
                        bookingBtn
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        card.getChildren().addAll(
                name,
                category,
                capacity,
                desc,
                price,
                actions
        );

        content.getChildren().addAll(
                title,
                imageView,
                card
        );

        root.setCenter(
                ViewUtil.createScrollable(content)
        );

        return new Scene(root, 1280, 760);
    }
}