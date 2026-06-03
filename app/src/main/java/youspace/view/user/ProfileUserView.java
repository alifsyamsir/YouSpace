package youspace.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Stage;

import youspace.models.AppUser;

import youspace.service.UserService;

import youspace.utils.SessionManager;

public class ProfileUserView {

    private final Stage stage;

    public ProfileUserView(Stage stage) {

        this.stage = stage;
    }

    public Scene createScene() {

        StackPane wrapper = new StackPane();

        BorderPane root = new BorderPane();

        wrapper.getChildren().add(root);

        root.setStyle("""
            -fx-background-color:#F5F7FA;
        """);

        root.setLeft(
                new SidebarUser(stage, "Profile")
        );

        AppUser user = SessionManager.getCurrentUser();

        VBox content = new VBox(30);

        content.setPadding(new Insets(35));

        Label title = new Label("Profil Pengguna");

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        36
                )
        );

        title.setStyle("""
            -fx-text-fill:#183B63;
        """);

        HBox profileCard = new HBox(70);

        profileCard.setAlignment(Pos.CENTER_LEFT);

        profileCard.setPadding(new Insets(45));

        profileCard.setStyle("""
            -fx-background-color:white;
            -fx-background-radius:20;
            -fx-border-color:#E5E7EB;
            -fx-border-radius:20;
        """);

        // ================= LEFT =================

        VBox leftBox = new VBox(20);

        leftBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();

        avatar.setPrefSize(190, 190);

        avatar.setStyle("""
            -fx-background-color:#D9EAF7;
            -fx-background-radius:100;
        """);

        Label initials = new Label(
                user.getName()
                        .substring(0, 1)
                        .toUpperCase()
        );

        initials.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        72
                )
        );

        initials.setStyle("""
            -fx-text-fill:#183B63;
        """);

        avatar.getChildren().add(initials);

        Label username = new Label(user.getName());

        username.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        24
                )
        );

        leftBox.getChildren().addAll(
                avatar,
                username
        );

        // ================= RIGHT =================

        VBox rightBox = new VBox(24);

        rightBox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(
                rightBox,
                Priority.ALWAYS
        );

        Label name = createInfoLabel(
                "👤  " + user.getName()
        );

        Label email = createInfoLabel(
                "✉  " + user.getEmail()
        );

        Label phone = createInfoLabel(
                "📞  " + user.getPhone()
        );

        Button btnEdit = new Button("✎ Edit Profil");

        btnEdit.setPrefWidth(180);

        btnEdit.setPrefHeight(45);

        btnEdit.setStyle("""
            -fx-background-color:#183B63;
            -fx-text-fill:white;
            -fx-font-size:15;
            -fx-font-weight:bold;
            -fx-background-radius:12;
            -fx-cursor:hand;
        """);

        // ================= EDIT MODAL =================

        btnEdit.setOnAction(e -> {

            StackPane overlay = new StackPane();

            overlay.setStyle("""
                -fx-background-color:rgba(0,0,0,0.45);
            """);

            VBox modal = new VBox(25);

            modal.setMaxWidth(700);

            modal.setPadding(new Insets(35));

            modal.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:20;
            """);

            Label modalTitle = new Label("Edit Profil");

            modalTitle.setFont(
                    Font.font(
                            "System",
                            FontWeight.BOLD,
                            34
                    )
            );

            GridPane form = new GridPane();

            form.setHgap(20);

            form.setVgap(18);

            TextField tfName =
                    createField(user.getName());

            TextField tfPhone =
                    createField(user.getPhone());

            TextField tfEmail =
                    createField(user.getEmail());

            form.add(
                    createFormLabel("Nama"),
                    0,
                    0
            );

            form.add(
                    tfName,
                    0,
                    1
            );

            form.add(
                    createFormLabel("Nomor Telepon"),
                    1,
                    0
            );

            form.add(
                    tfPhone,
                    1,
                    1
            );

            form.add(
                    createFormLabel("Email"),
                    0,
                    2
            );

            form.add(
                    tfEmail,
                    0,
                    3,
                    2,
                    1
            );

            // ================= BUTTON =================

            HBox buttonBox = new HBox(20);

            buttonBox.setAlignment(Pos.CENTER);

            Button btnCancel = new Button("Batal");

            btnCancel.setPrefWidth(220);

            btnCancel.setPrefHeight(50);

            btnCancel.setStyle("""
                -fx-background-color:#E5E7EB;
                -fx-font-size:15;
                -fx-font-weight:bold;
                -fx-background-radius:14;
                -fx-cursor:hand;
            """);

            Button btnSave = new Button("Simpan");

            btnSave.setPrefWidth(220);

            btnSave.setPrefHeight(50);

            btnSave.setStyle("""
                -fx-background-color:#183B63;
                -fx-text-fill:white;
                -fx-font-size:15;
                -fx-font-weight:bold;
                -fx-background-radius:14;
                -fx-cursor:hand;
            """);

            btnCancel.setOnAction(ev -> {
                wrapper.getChildren().remove(overlay);
            });

            btnSave.setOnAction(ev -> {

                if (
                    tfName.getText().isBlank()
                    ||
                    tfPhone.getText().isBlank()
                    ||
                    tfEmail.getText().isBlank()
                ) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "Semua field wajib diisi!"
                    );

                    return;
                }

                user.setName(tfName.getText());

                user.setPhone(tfPhone.getText());

                user.setEmail(tfEmail.getText());

                UserService service =
                        new UserService();

                boolean success =
                        service.updateProfile(user);

                if (success) {

                    SessionManager.setCurrentUser(user);

                    stage.setScene(
                            new ProfileUserView(stage)
                                    .createScene()
                    );

                } else {

                    showAlert(
                            Alert.AlertType.ERROR,
                            "Gagal update profile!"
                    );
                }
            });

            buttonBox.getChildren().addAll(
                    btnCancel,
                    btnSave
            );

            modal.getChildren().addAll(
                    modalTitle,
                    form,
                    buttonBox
            );

            overlay.getChildren().add(modal);

            wrapper.getChildren().add(overlay);
        });

        rightBox.getChildren().addAll(
                name,
                email,
                phone,
                btnEdit
        );

        profileCard.getChildren().addAll(
                leftBox,
                rightBox
        );

        content.getChildren().addAll(
                title,
                profileCard
        );

        root.setCenter(content);

        return new Scene(
                wrapper,
                1280,
                760
        );
    }

    private Label createInfoLabel(String text) {

        Label label = new Label(text);

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.MEDIUM,
                        20
                )
        );

        label.setTextFill(
                Color.web("#2D3748")
        );

        return label;
    }

    private Label createFormLabel(String text) {

        Label label = new Label(text);

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.SEMI_BOLD,
                        14
                )
        );

        return label;
    }

    private TextField createField(String value) {

        TextField field =
                new TextField(value);

        field.setPrefHeight(45);

        field.setStyle("""
            -fx-background-color:#F3F4F6;
            -fx-background-radius:10;
            -fx-border-radius:10;
            -fx-font-size:14;
        """);

        return field;
    }

    private void showAlert(
            Alert.AlertType type,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}