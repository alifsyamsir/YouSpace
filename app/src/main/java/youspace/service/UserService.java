package youspace.service;

import java.util.List;

import youspace.dao.UserDAO;
import youspace.models.AppUser;
import youspace.utils.SessionManager;
import youspace.utils.ValidationUtil;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // ================= METHOD TAMBAHAN UNTUK LOGIN CONTROLLER =================
    public AppUser findByEmail(String email) {
        if (ValidationUtil.isEmpty(email)) {
            throw new IllegalArgumentException("Email tidak boleh kosong.");
        }
        // Meneruskan pencarian data kredensial ke layer data access (UserDAO)
        return userDAO.findByEmail(email);
    }
    // =========================================================================

    public AppUser getUserById(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID tidak valid.");
        }

        return userDAO.findById(userId);
    }

    public List<AppUser> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<AppUser> searchUsers(String keyword) {
        if (ValidationUtil.isEmpty(keyword)) {
            return getAllUsers();
        }

        return userDAO.searchUsers(keyword);
    }

    public boolean updateProfile(AppUser user) {
        if (user == null) {
            throw new IllegalArgumentException("User tidak valid.");
        }

        if (ValidationUtil.isEmpty(user.getName())) {
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        }

        if (ValidationUtil.isEmpty(user.getPhone())) {
            throw new IllegalArgumentException("Nomor HP tidak boleh kosong.");
        }

        boolean success = userDAO.updateProfile(user);

        if (
            success &&
            SessionManager.getCurrentUser() != null &&
            SessionManager.getCurrentUser().getId() == user.getId()
        ) {
            SessionManager.getCurrentUser().setName(user.getName());
            SessionManager.getCurrentUser().setPhone(user.getPhone());
            SessionManager.getCurrentUser().setEmail(user.getEmail());
        }

        return success;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        AppUser currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalArgumentException("User belum login.");
        }

        if (currentUser.getId() != userId) {
            throw new IllegalArgumentException("Tidak bisa mengubah password user lain.");
        }

        if (
            ValidationUtil.isEmpty(oldPassword) ||
            ValidationUtil.isEmpty(newPassword) ||
            ValidationUtil.isEmpty(confirmPassword)
        ) {
            throw new IllegalArgumentException("Password lama, password baru, dan konfirmasi password wajib diisi.");
        }

        if (!currentUser.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Password lama salah.");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password tidak sesuai.");
        }

        boolean success = userDAO.updatePassword(userId, newPassword);

        if (success) {
            currentUser.setPassword(newPassword);
        }

        return success;
    }

    public boolean suspendUser(int userId) {
        return userDAO.suspendUser(userId);
    }

    public boolean activateUser(int userId) {
        return userDAO.activateUser(userId);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
}