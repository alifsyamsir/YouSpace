package youspace.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import youspace.dao.UserDAO;
import youspace.dao.VenueDAO;

import youspace.enums.BookingStatus;

import youspace.models.AppUser;
import youspace.models.Booking;
import youspace.models.Venue;

import youspace.service.BookingService;
import youspace.utils.ViewUtil;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BookingAdminView {

    private final Stage stage;

    private final BookingService bookingService;

    private final UserDAO userDAO;

    private final VenueDAO venueDAO;

    private TableView<Booking> tableView;

    private TextField tfSearch;

    public BookingAdminView(Stage stage) {

        this.stage = stage;

        this.bookingService =
                new BookingService();

        this.userDAO =
                new UserDAO();

        this.venueDAO =
                new VenueDAO();

        // AUTO COMPLETE BOOKING
        bookingService.autoCompleteBookings();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#F8F9FA;"
        );

        root.setLeft(
                new SidebarAdmin(
                        stage,
                        "Booking"
                )
        );

        VBox mainContent =
                new VBox(25);

        mainContent.setPadding(
                new Insets(35)
        );

        // ======================================================
        // HEADER
        // ======================================================

        HBox headerRow =
                new HBox();

        headerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "Kelola Booking"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        32
                )
        );

        title.setStyle(
                "-fx-text-fill:#1A365D;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        tfSearch =
                new TextField();

        tfSearch.setPromptText(
                "Cari Nama..."
        );

        tfSearch.setPrefWidth(240);

        tfSearch.setPrefHeight(42);

        tfSearch.setStyle("""
            -fx-background-radius:12;
            -fx-border-radius:12;
            -fx-border-color:#D1D5DB;
            -fx-background-color:white;
            -fx-padding:0 15 0 15;
            -fx-font-size:14;
        """);

        tfSearch.textProperty().addListener(
                (
                        observable,
                        oldValue,
                        newValue
                ) -> refreshTable(newValue)
        );

        headerRow.getChildren().addAll(
                title,
                spacer,
                tfSearch
        );

        // ======================================================
        // STATISTICS
        // ======================================================

        HBox statsRow =
                new HBox(20);

        long totalBooking =
                bookingService
                        .getAllBookings()
                        .size();

        long activeBooking =
                bookingService
                        .getAllBookings()
                        .stream()
                        .filter(
                                booking ->
                                        booking.getStatus()
                                        ==
                                        BookingStatus.APPROVED
                        )
                        .count();

        long completedBooking =
                bookingService
                        .getAllBookings()
                        .stream()
                        .filter(
                                booking ->
                                        booking.getStatus()
                                        ==
                                        BookingStatus.COMPLETED
                        )
                        .count();

        VBox cardAll =
                createStatCard(
                        totalBooking,
                        "Semua Booking",
                        "#1A365D",
                        "white"
                );

        VBox cardActive =
                createStatCard(
                        activeBooking,
                        "Booking Berlangsung",
                        "#D9EAF7",
                        "#1A365D"
                );

        VBox cardDone =
                createStatCard(
                        completedBooking,
                        "Booking Selesai",
                        "#D9EAF7",
                        "#1A365D"
                );

        statsRow.getChildren().addAll(
                cardAll,
                cardActive,
                cardDone
        );

        // ======================================================
        // TABLE
        // ======================================================

        tableView =
                new TableView<>();

        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tableView.setPrefHeight(500);

        tableView.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:15;
            -fx-border-radius:15;
            -fx-border-color:#E5E7EB;
        """);

        // ======================================================
        // NO
        // ======================================================

        TableColumn<Booking, Integer> colNo =
                new TableColumn<>("No.");

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNo.setMaxWidth(70);

        // ======================================================
        // USER
        // ======================================================

        TableColumn<Booking, String> colUser =
                new TableColumn<>("Nama Pemesan");

        colUser.setCellFactory(param ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            empty
                            ||
                            getTableRow() == null
                            ||
                            getTableRow().getItem() == null
                        ) {

                            setText(null);

                        } else {

                            Booking booking =
                                    getTableRow().getItem();

                            AppUser user =
                                    userDAO.findById(
                                            booking.getUserId()
                                    );

                            setText(
                                    user != null
                                    ?
                                    user.getName()
                                    :
                                    "-"
                            );
                        }
                    }
                });

        // ======================================================
        // VENUE
        // ======================================================

        TableColumn<Booking, String> colVenue =
                new TableColumn<>("Nama Venue");

        colVenue.setCellFactory(param ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            empty
                            ||
                            getTableRow() == null
                            ||
                            getTableRow().getItem() == null
                        ) {

                            setText(null);

                        } else {

                            Booking booking =
                                    getTableRow().getItem();

                            Venue venue =
                                    venueDAO.findById(
                                            booking.getVenueId()
                                    );

                            setText(
                                    venue != null
                                    ?
                                    venue.getName()
                                    :
                                    "-"
                            );
                        }
                    }
                });

        // ======================================================
        // TANGGAL
        // ======================================================

        TableColumn<Booking, String> colTanggal =
                new TableColumn<>("Tanggal");

        colTanggal.setCellFactory(param ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            empty
                            ||
                            getTableRow() == null
                            ||
                            getTableRow().getItem() == null
                        ) {

                            setText(null);

                        } else {

                            Booking booking =
                                    getTableRow().getItem();

                            setText(
                                    booking.getStartDate()
                                    +
                                    " - "
                                    +
                                    booking.getEndDate()
                            );
                        }
                    }
                });

        // ======================================================
        // STATUS
        // ======================================================

        TableColumn<Booking, BookingStatus> colStatus =
                new TableColumn<>("Status");

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        colStatus.setCellFactory(param ->
                new TableCell<>() {

                    private final Label badge =
                            new Label();

                    @Override
                    protected void updateItem(
                            BookingStatus item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            empty
                            ||
                            item == null
                        ) {

                            setGraphic(null);

                        } else {

                            if (
                                item ==
                                BookingStatus.APPROVED
                            ) {

                                badge.setText(
                                        "Berlangsung"
                                );

                                badge.setStyle("""
                                    -fx-background-color:#D6EAF8;
                                    -fx-text-fill:#1A365D;
                                    -fx-background-radius:20;
                                    -fx-padding:6 18 6 18;
                                    -fx-font-weight:bold;
                                """);

                            } else if (
                                item ==
                                BookingStatus.COMPLETED
                            ) {

                                badge.setText(
                                        "Selesai"
                                );

                                badge.setStyle("""
                                    -fx-background-color:#C6F6D5;
                                    -fx-text-fill:#2F855A;
                                    -fx-background-radius:20;
                                    -fx-padding:6 18 6 18;
                                    -fx-font-weight:bold;
                                """);

                            } else {

                                badge.setText(
                                        item.name()
                                );

                                badge.setStyle("""
                                    -fx-background-color:#FEF3C7;
                                    -fx-text-fill:#92400E;
                                    -fx-background-radius:20;
                                    -fx-padding:6 18 6 18;
                                    -fx-font-weight:bold;
                                """);
                            }

                            setGraphic(badge);
                        }
                    }
                });

        // ======================================================
        // DETAIL BUTTON
        // ======================================================

        TableColumn<Booking, Void> colDetail =
                new TableColumn<>("Detail");

        colDetail.setCellFactory(param ->
                new TableCell<>() {

                    private final Button btn =
                            new Button("➜");

                    {

                        btn.setStyle("""
                            -fx-background-color:white;
                            -fx-border-color:#D1D5DB;
                            -fx-background-radius:50;
                            -fx-border-radius:50;
                            -fx-font-size:16;
                            -fx-cursor:hand;
                        """);

                        btn.setOnAction(e -> {

                            Booking booking =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            Alert alert =
                                    new Alert(
                                            Alert.AlertType.INFORMATION
                                    );

                            alert.setHeaderText(
                                    "Detail Booking"
                            );

                            alert.setContentText(
                                    "Booking ID : "
                                    + booking.getId()
                                    +
                                    "\nTanggal : "
                                    + booking.getStartDate()
                                    + " - "
                                    + booking.getEndDate()
                                    +
                                    "\nTotal : Rp"
                                    + String.format(
                                            "%,.0f",
                                            booking.getTotalPrice()
                                    )
                            );

                            alert.showAndWait();
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(btn);
                        }
                    }
                });

        // ======================================================
        // COMPLETE BUTTON
        // ======================================================

        TableColumn<Booking, Void> colAction =
                new TableColumn<>("Aksi");

        colAction.setCellFactory(param ->
                new TableCell<>() {

                    private final Button btn =
                            new Button("Selesai");

                    {

                        btn.setStyle("""
                            -fx-background-color:#1A365D;
                            -fx-text-fill:white;
                            -fx-background-radius:8;
                            -fx-font-weight:bold;
                            -fx-cursor:hand;
                        """);

                        btn.setOnAction(e -> {

                            Booking booking =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            Alert confirm =
                                    new Alert(
                                            Alert.AlertType.CONFIRMATION
                                    );

                            confirm.setHeaderText(
                                    "Konfirmasi"
                            );

                            confirm.setContentText(
                                    "Tandai booking selesai?"
                            );

                            Optional<ButtonType> result =
                                    confirm.showAndWait();

                            if (
                                result.isPresent()
                                &&
                                result.get()
                                ==
                                ButtonType.OK
                            ) {

                                bookingService.completeBooking(
                                        booking.getId()
                                );

                                refreshTable(
                                        tfSearch.getText()
                                );
                            }
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            empty
                        ) {

                            setGraphic(null);

                        } else {

                            Booking booking =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if (
                                booking.getStatus()
                                ==
                                BookingStatus.COMPLETED
                            ) {

                                setGraphic(null);

                            } else {

                                setGraphic(btn);
                            }
                        }
                    }
                });

        tableView.getColumns().clear();

        tableView.getColumns().add(colNo);
        tableView.getColumns().add(colUser);
        tableView.getColumns().add(colVenue);
        tableView.getColumns().add(colTanggal);
        tableView.getColumns().add(colStatus);
        tableView.getColumns().add(colDetail);
        tableView.getColumns().add(colAction);

        // ======================================================
        // LOAD DATA
        // ======================================================

        refreshTable("");

        mainContent.getChildren().addAll(
                headerRow,
                statsRow,
                tableView
        );

        root.setCenter(
                ViewUtil.createScrollable(mainContent)
        );

        return new Scene(
                root,
                1280,
                760
        );
    }

    // ======================================================
    // REFRESH TABLE
    // ======================================================

    private void refreshTable(
            String keyword
    ) {

        tableView.getItems().clear();

        List<Booking> bookings;

        if (
            keyword == null
            ||
            keyword.isBlank()
        ) {

            bookings =
                    bookingService.getAllBookings();

        } else {

            bookings =
                    bookingService.searchBookingsByUserName(
                            keyword
                    );
        }

        tableView.getItems().addAll(
                bookings
        );
    }

    // ======================================================
    // CARD
    // ======================================================

    private VBox createStatCard(
            long value,
            String label,
            String bgColor,
            String textColor
    ) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color:"
                + bgColor
                +
                ";"
                +
                "-fx-background-radius:18;"
        );

        Label number =
                new Label(
                        String.valueOf(value)
                );

        number.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        42
                )
        );

        number.setStyle(
                "-fx-text-fill:"
                + textColor
                +
                ";"
        );

        Label text =
                new Label(label);

        text.setStyle(
                "-fx-text-fill:"
                + textColor
                +
                ";"
        );

        text.setFont(
                Font.font(
                        "System",
                        FontWeight.MEDIUM,
                        14
                )
        );

        card.getChildren().addAll(
                number,
                text
        );

        return card;
    }
}