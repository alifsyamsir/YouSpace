package youspace.view.user;

import java.util.List;

import javafx.geometry.Insets;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import youspace.enums.BookingStatus;

import youspace.models.Booking;
import youspace.models.Venue;

import youspace.service.BookingService;
import youspace.service.VenueService;

import youspace.utils.SessionManager;

public class BookingUserView {

    private final Stage stage;

    private final BookingService bookingService;

    private final VenueService venueService;

    private GridPane grid;

    private String filter = "Semua";

    public BookingUserView(Stage stage) {

        this.stage = stage;

        bookingService = new BookingService();

        venueService = new VenueService();
    }

    public Scene createScene() {

        bookingService.autoCompleteBookings();

        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color:#F8FAFC;");

        root.setLeft(new SidebarUser(stage, "Booking"));

        VBox content = new VBox(25);

        content.setPadding(new Insets(35));

        Label title = new Label("Booking");

        title.setFont(Font.font("System", FontWeight.BOLD, 40));

        HBox filters = new HBox(14);

        Button all = createFilter("Semua");

        Button process = createFilter("Dalam Proses");

        Button done = createFilter("Selesai");

        filters.getChildren().addAll(all, process, done);

        grid = new GridPane();

        grid.setHgap(25);

        grid.setVgap(25);

        loadBookings();

        ScrollPane scroll = new ScrollPane(grid);

        scroll.setFitToWidth(true);

        scroll.setStyle("-fx-background-color:transparent;");

        content.getChildren().addAll(title, filters, scroll);

        root.setCenter(content);

        return new Scene(root, 1280, 760);
    }

    private void loadBookings() {

        grid.getChildren().clear();

        List<Booking> bookings = bookingService.getBookingsByUser(
                SessionManager.getCurrentUser().getId()
        );

        int col = 0;

        int row = 0;

        for (Booking booking : bookings) {

            if (
                filter.equals("Dalam Proses")
                &&
                booking.getStatus() != BookingStatus.APPROVED
            ) {
                continue;
            }

            if (
                filter.equals("Selesai")
                &&
                booking.getStatus() != BookingStatus.COMPLETED
            ) {
                continue;
            }

            Venue venue = venueService.getVenueById(booking.getVenueId());

            if (venue == null) {
                continue;
            }

            VBox card = new VenueUserView(stage).createVenueCard(venue);

            grid.add(card, col, row);

            col++;

            if (col == 2) {

                col = 0;

                row++;
            }
        }
    }

    private Button createFilter(String text) {

        Button btn = new Button(text);

        styleFilter(btn, text.equals(filter));

        btn.setOnAction(e -> {

            filter = text;

            loadBookings();
        });

        return btn;
    }

    private void styleFilter(Button btn, boolean active) {

        if (active) {

            btn.setStyle("""
                -fx-background-color:#183B63;
                -fx-text-fill:white;
                -fx-background-radius:12;
                -fx-padding:10 24;
                -fx-font-size:14;
                -fx-font-weight:bold;
                -fx-cursor:hand;
            """);

        } else {

            btn.setStyle("""
                -fx-background-color:#D9EAF7;
                -fx-text-fill:#183B63;
                -fx-background-radius:12;
                -fx-padding:10 24;
                -fx-font-size:14;
                -fx-font-weight:bold;
                -fx-cursor:hand;
            """);
        }
    }
}