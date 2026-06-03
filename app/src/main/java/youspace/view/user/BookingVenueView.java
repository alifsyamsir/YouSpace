package youspace.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import youspace.enums.PaymentMethod;

import youspace.models.Booking;
import youspace.models.Venue;

import youspace.service.BookingService;

import youspace.utils.SessionManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import youspace.utils.ViewUtil;

public class BookingVenueView {

    private final Stage stage;

    private final Venue venue;

    private final BookingService bookingService;

    public BookingVenueView(
            Stage stage,
            Venue venue
    ) {

        this.stage = stage;

        this.venue = venue;

        this.bookingService =
                new BookingService();
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

        VBox content =
                new VBox(25);

        content.setPadding(
                new Insets(35)
        );

        Label title =
                new Label(
                        "Konfirmasi Pesanan"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        36
                )
        );

        // ================= BOOKED DATE =================

        Set<LocalDate> bookedDates =
                new HashSet<>();

        List<String> booked =
                bookingService
                        .getBookedDatesByVenue(
                                venue.getId()
                        );

        for (String item : booked) {

            String[] split =
                    item.split(" sampai ");

            LocalDate start =
                    LocalDate.parse(split[0]);

            LocalDate end =
                    LocalDate.parse(split[1]);

            while (!start.isAfter(end)) {

                bookedDates.add(start);

                start = start.plusDays(1);
            }
        }

        // ================= DATE PICKER =================

        DatePicker startDate =
                new DatePicker();

        DatePicker endDate =
                new DatePicker();

        styleDatePicker(
                startDate,
                bookedDates
        );

        styleDatePicker(
                endDate,
                bookedDates
        );

        // ================= PAYMENT =================

        ToggleGroup paymentGroup =
                new ToggleGroup();

        ToggleButton btnQris =
                new ToggleButton("QRIS");

        ToggleButton btnBank =
                new ToggleButton("Transfer Bank");

        btnQris.setToggleGroup(paymentGroup);

        btnBank.setToggleGroup(paymentGroup);

        btnQris.setSelected(true);

        stylePaymentButton(btnQris, true);
        stylePaymentButton(btnBank, false);

        btnQris.setOnAction(e -> {

            stylePaymentButton(btnQris, true);
            stylePaymentButton(btnBank, false);
        });

        btnBank.setOnAction(e -> {

            stylePaymentButton(btnBank, true);
            stylePaymentButton(btnQris, false);
        });

        HBox paymentBox =
                new HBox(
                        12,
                        btnQris,
                        btnBank
                );

        // ================= TOTAL =================

        Label totalLabel =
                new Label("Rp0");

        totalLabel.setStyle("""
            -fx-text-fill:#F97316;
            -fx-font-size:34;
            -fx-font-weight:bold;
        """);

        final double[] totalPrice = {0};

        Runnable calculate = () -> {

            if (
                startDate.getValue() != null
                &&
                endDate.getValue() != null
            ) {

                long days =
                        ChronoUnit.DAYS.between(
                                startDate.getValue(),
                                endDate.getValue()
                        ) + 1;

                totalPrice[0] =
                        days *
                        venue.getPricePerDay();

                totalLabel.setText(
                        "Rp "
                        + String.format(
                            "%,.0f",
                            totalPrice[0]
                        )
                );
            }
        };

        startDate.setOnAction(e -> calculate.run());

        endDate.setOnAction(e -> calculate.run());

        // ================= BUTTON =================

        Button btnBooking =
                new Button(
                        "Buat Pesanan"
                );

        btnBooking.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-size:15;
            -fx-font-weight:bold;
            -fx-padding:14 28;
            -fx-background-radius:12;
        """);

        btnBooking.setOnAction(e -> {

            if (
                startDate.getValue() == null
                ||
                endDate.getValue() == null
            ) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Pilih tanggal booking."
                ).show();

                return;
            }

            Booking booking =
                    bookingService
                    .createBookingDirect(
                            SessionManager
                                    .getCurrentUser()
                                    .getId(),

                            venue.getId(),

                            startDate
                                    .getValue()
                                    .toString(),

                            endDate
                                    .getValue()
                                    .toString(),

                            totalPrice[0]
                    );

            if (btnQris.isSelected()) {

                stage.setScene(
                        new QrisPaymentView(
                                stage,
                                booking
                        ).createScene()
                );

            } else {

                stage.setScene(
                        new BankPaymentView(
                                stage,
                                booking
                        ).createScene()
                );
            }
        });

        VBox form =
                new VBox(20);

        form.setPadding(
                new Insets(25)
        );

        form.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:18;
        """);

        form.getChildren().addAll(
                new Label("Tanggal Mulai"),
                startDate,

                new Label("Tanggal Selesai"),
                endDate,

                new Label("Metode Pembayaran"),
                paymentBox,

                totalLabel,

                btnBooking
        );

        content.getChildren().addAll(
                title,
                form
        );

        root.setCenter(content);

        return new Scene(root, 1280, 760);
    }

    private void styleDatePicker(
            DatePicker picker,
            Set<LocalDate> bookedDates
    ) {

        picker.setDayCellFactory(dp ->
                new DateCell() {

                    @Override
                    public void updateItem(
                            LocalDate item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (
                            item != null
                            &&
                            bookedDates.contains(item)
                        ) {

                            setDisable(true);

                            setStyle("""
                                -fx-background-color:#183B63;
                                -fx-text-fill:white;
                            """);
                        }
                    }
                });
    }

    private void stylePaymentButton(
            ToggleButton btn,
            boolean active
    ) {

        if (active) {

            btn.setStyle("""
                -fx-background-color:#183B63;
                -fx-text-fill:white;
                -fx-background-radius:10;
                -fx-padding:10 20;
            """);

        } else {

            btn.setStyle("""
                -fx-background-color:#E2E8F0;
                -fx-text-fill:black;
                -fx-background-radius:10;
                -fx-padding:10 20;
            """);
        }
    }
}