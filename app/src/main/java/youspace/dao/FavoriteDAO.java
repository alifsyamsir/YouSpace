package youspace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import youspace.config.DatabaseConfig;
import youspace.enums.VenueCategory;
import youspace.enums.VenueStatus;
import youspace.models.Venue;

public class FavoriteDAO {

    public boolean addFavorite(int userId, int venueId) {
        String sql = "INSERT OR IGNORE INTO favorites (user_id, venue_id) VALUES (?, ?);";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, venueId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal menambahkan favorit: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFavorite(int userId, int venueId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND venue_id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, venueId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal menghapus favorit: " + e.getMessage());
            return false;
        }
    }

    public List<Venue> getFavoriteVenuesByUser(int userId) {
        List<Venue> venues = new ArrayList<>();

        String sql = """
            SELECT v.*
            FROM favorites f
            JOIN venues v ON f.venue_id = v.id
            WHERE f.user_id = ?
            ORDER BY f.id DESC;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                venues.add(new Venue(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    VenueCategory.valueOf(rs.getString("category")),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_day"),
                    rs.getString("image_path"),
                    VenueStatus.valueOf(rs.getString("status"))
                ));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil venue favorit: " + e.getMessage());
        }

        return venues;
    }

    public int countFavoritesByUser(int userId) {
    String sql = """
        SELECT COUNT(*) AS total
        FROM favorites
        WHERE user_id = ?;
        """;

  
        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Gagal menghitung wishlist user: " + e.getMessage());
        }

        return 0;
    }

    public boolean isFavorite(int userId, int venueId) {

        String sql = """
        SELECT * FROM favorites
        WHERE user_id = ?
        AND venue_id = ?;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, userId);
            stmt.setInt(2, venueId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {

            System.out.println(
                "Gagal cek favorite: "
                + e.getMessage()
            );
        }

        return false;
    }
}