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
import youspace.controllers.LoginController;

public class LoginView {

    private final Stage stage;
    private final LoginController controller;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.controller = new LoginController(this);
    }

    public Scene createScene() {
        // Main Container Split 2 Kolom
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
        Label titleLabel = new Label("Masuk ke YouSpace");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #1A202C;");

        Text descText = new Text("Yuk, masuk dan temukan ruang terbaik untuk acaramu hari ini.");
        descText.setWrappingWidth(300);
        descText.setFont(Font.font("System", 14));
        descText.setStyle("-fx-fill: #718096;");

        leftFooter.getChildren().addAll(titleLabel, descText);
        
        // Memisahkan brand di atas dan teks deskripsi di bawah
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        leftColumn.getChildren().addAll(brandLabel, spacer, leftFooter);

        // ================= KOLOM KANAN (Form Input) =================
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(500);
        rightColumn.setPadding(new Insets(50));
        rightColumn.setAlignment(Pos.CENTER_LEFT);
        rightColumn.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20px 0px 0px 20px;");

        // Label Form Sign In kecil di atas
        Label signInHint = new Label("Sign In");
        signInHint.setFont(Font.font("System", 12));
        signInHint.setStyle("-fx-text-fill: #A0AEC0;");

        // Input Email / Nama
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("Email / Nama");
        emailLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Masukkan email Anda");
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Input Kata Sandi
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Kata Sandi");
        passwordLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan kata Anda");
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10; -fx-padding: 0 15 0 15;");
        
        passwordBox.getChildren().addAll(
            passwordLabel,
            passwordField
        );

        // Tombol Masuk
        Button loginButton = new Button("Masuk");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(45);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");

        // Trigger Controller saat tombol diklik
        loginButton.setOnAction(e -> controller.handleLogin(emailField.getText(), passwordField.getText()));

        // Teks Beralih ke Sign Up
        HBox signUpHintBox = new HBox(5);
        signUpHintBox.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Belum punya akun?");
        noAccountLabel.setStyle("-fx-text-fill: #718096;");
        Hyperlink signUpLink = new Hyperlink("Daftar di sini!");
        signUpLink.setStyle("-fx-text-fill: #3182CE; -fx-padding: 0; -fx-underline: false;");
        
        // Pindah ke Halaman Register
        signUpLink.setOnAction(e -> {
            try {
                RegisterView registerView = new RegisterView(stage);
                stage.setScene(registerView.createScene());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(
                    "Error",
                    "Gagal membuka halaman registrasi:\n" + ex.getMessage(),
                    Alert.AlertType.ERROR
                );
            }
        });
        signUpHintBox.getChildren().addAll(noAccountLabel, signUpLink);

        rightColumn.getChildren().addAll(signInHint, emailBox, passwordBox, loginButton, signUpHintBox);

        // Satukan kedua kolom ke dalam layout utama
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