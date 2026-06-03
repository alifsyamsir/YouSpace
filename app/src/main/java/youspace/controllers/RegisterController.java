package youspace.controllers;

import javafx.scene.control.Alert;
import youspace.service.AuthService;
import youspace.view.LoginView;
import youspace.view.RegisterView;

public class RegisterController {

    private final RegisterView view;
    private final AuthService authService;

    public RegisterController(RegisterView view) {
        this.view = view;
        this.authService = new AuthService();
    }

    public void handleRegister(String name, String email, String password, String phone) {
        // Mencegah email statis admin didaftarkan ulang oleh orang lain via Sign Up
        if ("admin@gmail.com".equalsIgnoreCase(email.trim())) {
            view.showAlert("Gagal Daftar", "Email ini dilindungi sistem dan tidak bisa digunakan.", Alert.AlertType.ERROR);
            return;
        }

        try {
            boolean success = authService.register(name, email, password, phone);
            if (success) {
                view.showAlert("Sukses", "Pendaftaran berhasil! Silakan login menggunakan akun baru Anda.", Alert.AlertType.INFORMATION);
                
                // Redirect otomatis ke Halaman Login setelah sukses sign up
                LoginView loginView = new LoginView(view.getStage());
                view.getStage().setScene(loginView.createScene());
            }
        } catch (IllegalArgumentException ex) {
            view.showAlert("Gagal Daftar", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            view.showAlert("Error", "Terjadi kegagalan pendaftaran: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}