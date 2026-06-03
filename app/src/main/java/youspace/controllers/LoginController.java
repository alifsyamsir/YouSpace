package youspace.controllers;

import javafx.scene.control.Alert;
import youspace.enums.UserRole;
import youspace.enums.UserStatus;
import youspace.models.Admin; // Pastikan model Admin di-import
import youspace.models.AppUser;
import youspace.service.UserService;
import youspace.utils.SessionManager;
import youspace.view.LoginView;
import youspace.view.admin.AdminDashboardView; 
import youspace.view.user.UserDashboardView;

public class LoginController {

    private final LoginView view;
    private final UserService userService;

    public LoginController(LoginView view) {
        this.view = view;
        this.userService = new UserService();
    }

    public void handleLogin(String email, String password) {
        // 1. Validasi jika field input kosong
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            view.showAlert("Login Gagal", "Email dan Kata Sandi wajib diisi!", Alert.AlertType.ERROR);
            return;
        }

        // ================= ATURAN KHUSUS LOGIN ADMIN =================
        if (email.trim().equals("admin@gmail.com") && password.equals("OOP5SUKSES")) {
            // Membuat objek mock Admin instan sesuai aturan awalmu
            AppUser adminUser = new Admin(
                0,
                "Super Admin",
                "admin@gmail.com",
                "OOP5SUKSES",
                "-",
                UserStatus.ACTIVE
            );
            
            // Simpan ke session manager
            SessionManager.setCurrentUser(adminUser);
            
            view.showAlert("Login Sukses", "Selamat datang Admin, Super Admin!", Alert.AlertType.INFORMATION);
            
            // Langsung alihkan ke Dashboard Admin View
            AdminDashboardView adminDashboard = new AdminDashboardView(view.getStage());
            view.getStage().setScene(adminDashboard.createScene());
            view.getStage().centerOnScreen();
            return; // Hentikan eksekusi di sini agar tidak masuk ke pengecekan database user biasa
        }
        // =============================================================

        // 2. JALUR LOGIN USER BISA (Menggunakan Database)
        try {
            AppUser user = userService.findByEmail(email);

            //login gagal jika email tidak ditemukan
            if (user == null) {
                view.showAlert(
                    "Login Gagal",
                    "Anda belum memiliki akun. Silakan daftar terlebih dahulu.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            SessionManager.setCurrentUser(user);

            //AKun suspended
            if (user.getStatus() == UserStatus.SUSPENDED) {
                view.showAlert(
                    "Login Gagal",
                    "Akun Anda sedang ditangguhkan oleh Admin.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            //Login SUkses
            view.showAlert(
                "Login Sukses",
                "Selamat datang, " + user.getName() + "!",
                Alert.AlertType.INFORMATION
            );

            UserDashboardView dashboard =
            new UserDashboardView(view.getStage());

            view.getStage().setScene(
                dashboard.createScene()
            );

            view.getStage().centerOnScreen();
           
        } catch (Exception e) {
            view.showAlert(
                "Error",
                "Terjadi kesalahan sistem: " + e.getMessage(),
                Alert.AlertType.ERROR
            );
            e.printStackTrace();
        }
    }
}
    