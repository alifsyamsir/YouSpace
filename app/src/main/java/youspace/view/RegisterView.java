package youspace.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import youspace.controllers.RegisterController;

public class RegisterView {

    private final Stage stage;
    private final RegisterController controller;

    public RegisterView(Stage stage) {
        this.stage = stage;
        this.controller = new RegisterController(this);
    }

    public Scene createScene() {
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: #F8F9FA;");
        mainLayout.setPrefSize(900, 550);

        // ================= KOLOM KIRI (Brand Message) =================
        VBox leftColumn = new VBox(25);
        leftColumn.setPrefWidth(400);
        leftColumn.setPadding(new Insets(50));
        leftColumn.setAlignment(Pos.CENTER_LEFT);

        Label brandLabel = new Label("YouSpace");
        brandLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        brandLabel.setStyle("-fx-text-fill: #1A202C;");

        VBox leftFooter = new VBox(10);
        Label titleLabel = new Label("Buat akun YouSpace");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #1A202C;");

        Text descText = new Text("Temukan ruang paling pas buat setiap momen seru kamu. Your event, your choice, YourSpace.");
        descText.setWrappingWidth(300);
        descText.setFont(Font.font("System", 14));
        descText.setStyle("-fx-fill: #718096;");

        leftFooter.getChildren().addAll(titleLabel, descText);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        leftColumn.getChildren().addAll(brandLabel, spacer, leftFooter);

        // ================= KOLOM KANAN (Form Input) =================
        VBox rightColumn = new VBox(15);
        rightColumn.setPrefWidth(500);
        rightColumn.setPadding(new Insets(40, 50, 40, 50));
        rightColumn.setAlignment(Pos.CENTER_LEFT);
        rightColumn.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20px 0px 0px 20px;");

        Label signUpHint = new Label("Sign UP");
        signUpHint.setFont(Font.font("System", 12));
        signUpHint.setStyle("-fx-text-fill: #A0AEC0;");

        // Input Nama
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Nama");
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField nameField = new TextField();
        nameField.setPromptText("Masukkan nama Anda");
        nameField.setPrefHeight(40);
        nameField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Input Email
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField emailField = new TextField();
        emailField.setPromptText("Masukkan email Anda");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Input Kata Sandi
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Kata Sandi");
        passwordLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan kata Anda");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Input Nomor Telepon
        VBox phoneBox = new VBox(5);
        Label phoneLabel = new Label("Nomor Telpon");
        phoneLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField phoneField = new TextField();
        phoneField.setPromptText("Masukkan nomor telpon Anda");
        phoneField.setPrefHeight(40);
        phoneField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);

        // Tombol Daftar Sekarang
        Button registerButton = new Button("Daftar Sekarang");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(45);
        registerButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        registerButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");

        registerButton.setOnAction(e -> controller.handleRegister(
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                phoneField.getText()
        ));

        // Teks Kembali ke Sign In
        HBox signInHintBox = new HBox(5);
        signInHintBox.setAlignment(Pos.CENTER);
        Label alreadyHaveAccount = new Label("Sudah punya akun?");
        alreadyHaveAccount.setStyle("-fx-text-fill: #718096;");
        Hyperlink signInLink = new Hyperlink("Masuk di sini!");
        signInLink.setStyle("-fx-text-fill: #3182CE; -fx-padding: 0; -fx-underline: false;");
        
        signInLink.setOnAction(e -> {
            try {
                LoginView loginView = new LoginView(stage);
                stage.setScene(loginView.createScene());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(
                    "Error", "Gagal membuka halaman login:\n" + ex.getMessage(),
                    Alert.AlertType.ERROR
                );
            }
        });
        signInHintBox.getChildren().addAll(alreadyHaveAccount, signInLink);

        rightColumn.getChildren().addAll(signUpHint, nameBox, emailBox, passwordBox, phoneBox, registerButton, signInHintBox);

        mainLayout.getChildren().addAll(leftColumn, rightColumn);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        return new Scene(mainLayout, 900, 550);
    }

    public Stage getStage() {
        return stage;
    }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}