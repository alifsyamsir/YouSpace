package youspace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import youspace.config.DatabaseConfig;

import youspace.enums.UserRole;
import youspace.enums.UserStatus;

import youspace.models.Admin;
import youspace.models.AppUser;
import youspace.models.Customer;

public class UserDAO {

    public boolean register(Customer customer) {

        String sql = """
            INSERT INTO users
            (name, email, password, phone, role, status)
            VALUES (?, ?, ?, ?, ?, ?);
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getRole().name());
            stmt.setString(6, customer.getStatus().name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Register gagal: " + e.getMessage());

            return false;
        }
    }

    public AppUser findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {

            System.out.println("Gagal mencari user: " + e.getMessage());
        }

        return null;
    }

    public AppUser findById(int userId) {

        String sql = "SELECT * FROM users WHERE id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {

            System.out.println("Gagal mencari user berdasarkan ID: " + e.getMessage());
        }

        return null;
    }

    public List<AppUser> getAllUsers() {

        List<AppUser> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id DESC;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {

            System.out.println("Gagal mengambil data user: " + e.getMessage());
        }

        return users;
    }

    public List<AppUser> searchUsers(String keyword) {

        List<AppUser> users = new ArrayList<>();

        String sql = """
            SELECT * FROM users
            WHERE name LIKE ?
            OR email LIKE ?
            OR phone LIKE ?
            ORDER BY id DESC;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            String searchKeyword = "%" + keyword + "%";

            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            stmt.setString(3, searchKeyword);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {

            System.out.println("Gagal mencari user: " + e.getMessage());
        }

        return users;
    }

    public boolean updateProfile(AppUser user) {

        String sql = """
            UPDATE users
            SET name = ?,
                email = ?,
                phone = ?
            WHERE id = ?;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Gagal update profile: " + e.getMessage());

            return false;
        }
    }

    public boolean updatePassword(int userId, String newPassword) {

        String sql = """
            UPDATE users
            SET password = ?
            WHERE id = ?;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Gagal mengubah password: " + e.getMessage());

            return false;
        }
    }

    public boolean suspendUser(int userId) {

        String sql = """
            UPDATE users
            SET status = 'SUSPENDED'
            WHERE id = ?
            AND role = 'USER';
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Gagal suspend user: " + e.getMessage());

            return false;
        }
    }

    public boolean activateUser(int userId) {

        String sql = """
            UPDATE users
            SET status = 'ACTIVE'
            WHERE id = ?
            AND role = 'USER';
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Gagal aktivasi user: " + e.getMessage());

            return false;
        }
    }

    public boolean deleteUser(int userId) {

        String sql = """
            DELETE FROM users
            WHERE id = ?
            AND role = 'USER';
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Gagal hapus user: " + e.getMessage());

            return false;
        }
    }

    private AppUser mapResultSetToUser(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");

        String name = rs.getString("name");

        String email = rs.getString("email");

        String password = rs.getString("password");

        String phone = rs.getString("phone");

        UserRole role = UserRole.valueOf(rs.getString("role"));

        UserStatus status = UserStatus.valueOf(rs.getString("status"));

        AppUser user;
        
        if (role == UserRole.ADMIN) {

            user = new Admin(
                id,
                name,
                email,
                password,
                phone,
                status    
            );
    
        } else {

            user = new Customer(
                id,
                name,
                email,
                password,
                phone,
                status
            );
        }
        
        return user;
    }
}