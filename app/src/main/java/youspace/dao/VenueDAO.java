package youspace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import youspace.config.DatabaseConfig;
import youspace.enums.VenueCategory;
import youspace.enums.VenueStatus;
import youspace.models.Venue;

public class VenueDAO {

    public boolean addVenue(Venue venue) {
        String sql = """
            INSERT INTO venues 
            (name, description, category, capacity, price_per_day, image_path, status)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getDescription());
            stmt.setString(3, venue.getCategory().name());
            stmt.setInt(4, venue.getCapacity());
            stmt.setDouble(5, venue.getPricePerDay());
            stmt.setString(6, venue.getImagePath());
            stmt.setString(7, venue.getStatus().name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal tambah venue: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVenue(Venue venue) {
        String sql = """
            UPDATE venues
            SET name = ?, description = ?, category = ?, capacity = ?, price_per_day = ?, image_path = ?, status = ?
            WHERE id = ?;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getDescription());
            stmt.setString(3, venue.getCategory().name());
            stmt.setInt(4, venue.getCapacity());
            stmt.setDouble(5, venue.getPricePerDay());
            stmt.setString(6, venue.getImagePath());
            stmt.setString(7, venue.getStatus().name());
            stmt.setInt(8, venue.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal update venue: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVenue(int venueId) {
        String sql = "DELETE FROM venues WHERE id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, venueId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal hapus venue: " + e.getMessage());
            return false;
        }
    }

    public Venue findById(int venueId) {
        String sql = "SELECT * FROM venues WHERE id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, venueId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToVenue(rs);
            }

        } catch (SQLException e) {
            System.out.println("Gagal mencari venue: " + e.getMessage());
        }

        return null;
    }

    // --- METHOD BARU UNTUK FITUR REKOMENDASI ---
    public Venue getFirstVenueByCategory(String categoryName) {
        String sql = "SELECT * FROM venues WHERE category = ? ORDER BY id ASC LIMIT 1;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToVenue(rs);
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil venue rekomendasi: " + e.getMessage());
        }

        return null;
    }
    // ------------------------------------------

    public List<Venue> getAvailableVenues() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE status = ? ORDER BY id DESC;";
        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, VenueStatus.AVAILABLE.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengambil venue tersedia: " + e.getMessage());
        }
        return venues;
    }

    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY id DESC;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil venue: " + e.getMessage());
        }

        return venues;
    }


    public List<Venue> getRecommendedVenues() {

        List<Venue> venues = getAllVenues();

        venues.sort(
            Comparator.comparingDouble(
                Venue::getPricePerDay
            )   
        );

        if (venues.size() > 3) {
            return venues.subList(0, 3);   
        }

        return venues;
    }

    public List<Venue> getVenuesByCategory(VenueCategory category) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE category = ? ORDER BY id DESC;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, category.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil venue berdasarkan kategori: " + e.getMessage());
        }

        return venues;
    }

    public int countVenues() {
        String sql = "SELECT COUNT(*) AS total FROM venues;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Gagal menghitung venue: " + e.getMessage());
        }

        return 0;
    }

    private Venue mapResultSetToVenue(ResultSet rs) throws SQLException {
        return new Venue(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            VenueCategory.valueOf(rs.getString("category")),
            rs.getInt("capacity"),
            rs.getDouble("price_per_day"),
            rs.getString("image_path"),
            VenueStatus.valueOf(rs.getString("status"))
        );
    }
}