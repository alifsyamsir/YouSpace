package youspace.view.user;

import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import youspace.enums.PaymentMethod;

import youspace.models.Booking;

import youspace.service.PaymentService;

public class QrisPaymentView {

    private final Stage stage;

    private final Booking booking;

    private final PaymentService paymentService;

    public QrisPaymentView(
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

        VBox box =
                new VBox(20);

        box.setAlignment(
                Pos.CENTER
        );

        Label title =
                new Label(
                        "Konfirmasi Pembayaran"
                );

        title.setStyle("""
            -fx-font-size:32;
            -fx-font-weight:bold;
        """);

        ImageView qr =
                new ImageView(
                        new Image(
                                "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=YouSpace"
                        )
                );

        Button done =
                new Button(
                        "Pembayaran Berhasil"
                );

        done.setStyle("""
            -fx-background-color:#F97316;
            -fx-text-fill:white;
            -fx-font-size:14;
            -fx-font-weight:bold;
            -fx-padding:12 24;
        """);

        done.setOnAction(e -> {

            paymentService.payBooking(
                    booking.getId(),
                    PaymentMethod.QRIS
            );

            stage.setScene(
                    new BookingUserView(stage)
                            .createScene()
            );
        });

        box.getChildren().addAll(
                title,
                qr,
                done
        );

        root.setCenter(box);

        return new Scene(root, 1280, 760);
    }
}