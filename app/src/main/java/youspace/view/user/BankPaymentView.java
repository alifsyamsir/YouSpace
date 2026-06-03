package youspace.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import youspace.enums.PaymentMethod;

import youspace.models.Booking;

import youspace.service.PaymentService;

public class BankPaymentView {

    private final Stage stage;

    private final Booking booking;

    private final PaymentService paymentService;

    public BankPaymentView(
            Stage stage,
            Booking booking
    ) {

        this.stage = stage;

        this.booking = booking;

        this.paymentService =
                new PaymentService();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle("""
            -fx-background-color:#F8FAFC;
        """);

        root.setLeft(
                new SidebarUser(stage, "Booking")
        );

        VBox card =
                new VBox(15);

        card.setAlignment(Pos.CENTER_LEFT);

        card.setPadding(
                new Insets(40)
        );

        card.setMaxWidth(500);

        card.setStyle("""
            -fx-background-color:#183B63;
            -fx-background-radius:20;
        """);

        Label bank =
                new Label(
                        "Bank Central Asia (BCA)"
                );

        bank.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:28;
            -fx-font-weight:bold;
        """);

        Label va =
                new Label(
                        "8899 0100 3312"
                );

        va.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:32;
            -fx-font-weight:bold;
        """);

        Label owner =
                new Label(
                        "a.n YouSpace"
                );

        owner.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:18;
        """);

        Label instruction =
                new Label("""
                1. Login mBanking
                2. Pilih Transfer BCA
                3. Masukkan nomor virtual account
                4. Bayar sesuai nominal
                """);

        instruction.setStyle("""
            -fx-text-fill:white;
        """);

        Button done =
                new Button(
                        "Pembayaran Berhasil"
                );

        done.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-padding:12 22;
        """);

        done.setOnAction(e -> {

            paymentService.payBooking(
                    booking.getId(),
                    PaymentMethod.TRANSFER_BANK
            );

            stage.setScene(
                    new BookingUserView(stage)
                            .createScene()
            );
        });

        card.getChildren().addAll(
                bank,
                va,
                owner,
                instruction,
                done
        );

        BorderPane.setAlignment(
                card,
                Pos.CENTER
        );

        root.setCenter(card);

        return new Scene(root, 1280, 760);
    }
}