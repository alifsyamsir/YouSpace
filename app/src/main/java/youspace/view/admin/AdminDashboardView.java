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
import youspace.service.DashboardService;
import youspace.utils.ViewUtil;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;
import youspace.utils.ViewUtil;

public class AdminDashboardView {

    private final Stage stage;

    private final DashboardService dashboardService;

    private final BookingService bookingService;

    private final UserDAO userDAO;

    private final VenueDAO venueDAO;

    public AdminDashboardView(Stage stage) {

        this.stage = stage;

        this.dashboardService =
                new DashboardService();

        this.bookingService =
                new BookingService();

        this.userDAO =
                new UserDAO();

        this.venueDAO =
                new VenueDAO();

        // AUTO UPDATE STATUS BOOKING
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
                        "Beranda"
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

        Label header =
                new Label(
                        "Beranda"
                );

        header.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        36
                )
        );

        header.setStyle(
                "-fx-text-fill:#1A365D;"
        );

        // ======================================================
        // STATS
        // ======================================================

        HBox statsRow =
                new HBox(20);

        int totalVenue =
                dashboardService.getTotalVenue();

        int bookingAktif =
                dashboardService.getActiveBooking();

        int totalBooking =
                dashboardService.getTotalBooking();

        VBox cardVenue =
                createStatCard(
                        String.valueOf(totalVenue),
                        "Total Venue"
                );

        VBox cardBookingAktif =
                createStatCard(
                        String.valueOf(bookingAktif),
                        "Booking Aktif"
                );

        VBox cardTotalBooking =
                createStatCard(
                        String.valueOf(totalBooking),
                        "Total Pemesan"
                );

        statsRow.getChildren().addAll(
                cardVenue,
                cardBookingAktif,
                cardTotalBooking
        );

        // ======================================================
        // TOTAL PENDAPATAN
        // ======================================================

        VBox incomeBox =
                new VBox(10);

        incomeBox.setAlignment(
                Pos.CENTER
        );

        incomeBox.setPadding(
                new Insets(30)
        );

        incomeBox.setStyle("""
            -fx-background-color:#D9EAF7;
            -fx-background-radius:20;
        """);

        double totalIncome =
                dashboardService.getTotalIncome();

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        String incomeText =
                rupiah.format(totalIncome)
                        .replace(",00", "");

        Label txtIncome =
                new Label(
                        incomeText
                );

        txtIncome.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        28
                )
        );

        Label txtIncomeTitle =
                new Label(
                        "Total Pendapatan"
                );

        txtIncomeTitle.setFont(
                Font.font(
                        "System",
                        FontWeight.MEDIUM,
                        16
                )
        );

        incomeBox.getChildren().addAll(
                txtIncome,
                txtIncomeTitle
        );

        // ======================================================
        // FILTER
        // ======================================================

        HBox filterRow =
                new HBox(15);

        Button btnCalendar =
                new Button(
                        "📅 Default"
                );

        btnCalendar.setPrefWidth(180);

        btnCalendar.setPrefHeight(42);

        btnCalendar.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#D1D5DB;
            -fx-background-radius:10;
            -fx-border-radius:10;
            -fx-font-size:14;
        """);

        ComboBox<String> filterBox =
                new ComboBox<>();

        filterBox.getItems().addAll(
                "Semua",
                "Berlangsung",
                "Selesai"
        );

        filterBox.setValue("Semua");

        filterBox.setPrefWidth(180);

        filterBox.setPrefHeight(42);

        filterBox.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#D1D5DB;
            -fx-background-radius:10;
            -fx-border-radius:10;
            -fx-font-size:14;
        """);

        filterRow.getChildren().addAll(
                btnCalendar,
                filterBox
        );

        // ======================================================
        // TABLE
        // ======================================================

        TableView<Booking> table =
                new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPrefHeight(320);

        table.setStyle("""
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
                                    -fx-padding:6 18 6 18;
                                    -fx-background-radius:20;
                                    -fx-font-weight:bold;
                                """);

                            } else {

                                badge.setText(
                                        "Selesai"
                                );

                                badge.setStyle("""
                                    -fx-background-color:#C6F6D5;
                                    -fx-text-fill:#2F855A;
                                    -fx-padding:6 18 6 18;
                                    -fx-background-radius:20;
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
                            -fx-border-radius:50;
                            -fx-background-radius:50;
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
                                    +
                                    " - "
                                    +
                                    booking.getEndDate()
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

        table.getColumns().clear();

        table.getColumns().add(colNo);
        table.getColumns().add(colUser);
        table.getColumns().add(colVenue);
        table.getColumns().add(colTanggal);
        table.getColumns().add(colStatus);
        table.getColumns().add(colDetail);

        // ======================================================
        // LOAD DATA
        // ======================================================

        List<Booking> bookings =
                bookingService.getAllBookings();

        table.getItems().addAll(bookings);

        // ======================================================
        // FILTER LOGIC
        // ======================================================

        filterBox.setOnAction(e -> {

            table.getItems().clear();

            String filter =
                    filterBox.getValue();

            for (Booking booking : bookings) {

                if (
                    filter.equals("Semua")
                ) {

                    table.getItems().add(booking);

                } else if (
                    filter.equals("Berlangsung")
                    &&
                    booking.getStatus()
                    ==
                    BookingStatus.APPROVED
                ) {

                    table.getItems().add(booking);

                } else if (
                    filter.equals("Selesai")
                    &&
                    booking.getStatus()
                    ==
                    BookingStatus.COMPLETED
                ) {

                    table.getItems().add(booking);
                }
            }
        });

        mainContent.getChildren().addAll(
                header,
                statsRow,
                incomeBox,
                filterRow,
                table
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
    // CARD
    // ======================================================

    private VBox createStatCard(
            String value,
            String label
    ) {

        VBox card =
                new VBox(10);

        card.setPrefWidth(240);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle("""
            -fx-background-color:#1A365D;
            -fx-background-radius:20;
        """);

        Label number =
                new Label(value);

        number.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        48
                )
        );

        number.setStyle(
                "-fx-text-fill:#F8F5E4;"
        );

        Label title =
                new Label(label);

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.MEDIUM,
                        15
                )
        );

        title.setStyle(
                "-fx-text-fill:white;"
        );

        card.getChildren().addAll(
                number,
                title
        );

        return card;
    }
}