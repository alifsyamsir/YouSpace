package youspace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import youspace.config.DatabaseConfig;
import youspace.enums.BookingStatus;
import youspace.models.Booking;

public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = """
            INSERT INTO bookings
            (user_id, venue_id, event_name, guest_count, start_date, end_date, total_price, status, note)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getVenueId());
            stmt.setString(3, booking.getEventName());
            stmt.setInt(4, booking.getGuestCount());
            stmt.setString(5, booking.getStartDate());
            stmt.setString(6, booking.getEndDate());
            stmt.setDouble(7, booking.getTotalPrice());
            stmt.setString(8, booking.getStatus().name());
            stmt.setString(9, booking.getNote());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal membuat booking: " + e.getMessage());
            return false;
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY id DESC;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil booking user: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY id DESC;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil semua booking: " + e.getMessage());
        }

        return bookings;
    }

    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }

        } catch (SQLException e) {
            System.out.println("Gagal mencari booking: " + e.getMessage());
        }

        return null;
    }

    public boolean updateStatus(int bookingId, BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?;";

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal update status booking: " + e.getMessage());
            return false;
        }
    }

    public boolean isVenueBookedInRange(int venueId, String startDate, String endDate) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM bookings
            WHERE venue_id = ?
              AND status IN ('PENDING', 'WAITING_PAYMENT', 'WAITING_CONFIRMATION', 'APPROVED')
              AND start_date <= ?
              AND end_date >= ?;
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, venueId);
            stmt.setString(2, endDate);
            stmt.setString(3, startDate);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.out.println("Gagal cek jadwal venue: " + e.getMessage());
        }

        return false;
    }

    
// Total seluruh booking yang pernah dibuat
    public int countAllBookings() {
        String sql = "SELECT COUNT(*) AS total FROM bookings;";


        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghitung total booking: " + e.getMessage()); 
        }

        return 0;
    }


// Booking aktif = sudah dibayar dan sedang berlangsung
    public int countActiveBookings() {
        String sql = """
        SELECT COUNT(*) AS total
        FROM bookings
        WHERE status = 'APPROVED';
        """;

  
        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            if (rs.next()) {  
                return rs.getInt("total");

            }

        } catch (SQLException e) {
            System.out.println("Gagal menghitung booking aktif: " + e.getMessage());  
        }

        return 0;
    }
  
    public int countActiveBookingsByUser(int userId) {
        String sql = """
        SELECT COUNT(*) AS total
        FROM bookings
        WHERE user_id = ?
        AND status = 'APPROVED';
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
            System.out.println("Gagal menghitung booking aktif user: " + e.getMessage());
        }

        return 0;
    }

    public int countBookingsByUser(int userId) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM bookings
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
            System.out.println("Gagal menghitung total booking user: " + e.getMessage());
        }

        return 0;
    }

    public void autoCompleteBookings() {

        String sql = """
        UPDATE bookings
        SET status = 'COMPLETED'
        WHERE status = 'APPROVED'
        AND date(end_date) < date('now');  
        """;

        try (
            Connection conn =  
            DatabaseConfig.getConnection();

            PreparedStatement stmt =
            conn.prepareStatement(sql)  
        ) {
  
            stmt.executeUpdate();
  
        } catch (SQLException e) {

            System.out.println(
                "Gagal auto complete booking: "
                + e.getMessage()
            );
        }
    }

    public List<String> getBookedDatesByVenue(int venueId) {

        List<String> bookedDates = new ArrayList<>();

        String sql = """
        SELECT start_date, end_date
        FROM bookings
        WHERE venue_id = ?
        AND status IN (
        'WAITING_PAYMENT',
        'WAITING_CONFIRMATION',
        'APPROVED'
        );
        """;
  
        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, venueId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                bookedDates.add(
                    rs.getString("start_date")
                    + " sampai "
                    + rs.getString("end_date")
                );
            }

        } catch (SQLException e) {
            System.out.println(
                "Gagal mengambil tanggal booking: "
                + e.getMessage()
            );  
        }

        return bookedDates;
    }

    // TOTAL BOOKING SELESAI
    public int countCompletedBookings() {

        String sql = """
        SELECT COUNT(*) AS total
        FROM bookings
        WHERE status = 'COMPLETED';
        """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {

            System.out.println(
                "Gagal menghitung booking selesai: "
                + e.getMessage()
            );
        }

        return 0;
    }

    
// SEARCH BOOKING BY USER

    public List<Booking> searchBookingsByUserName(
        String keyword
    ) {

        List<Booking> bookings =
        new ArrayList<>();

        String sql = """
        SELECT b.*
        FROM bookings b
        JOIN users u
        ON b.user_id = u.id
        WHERE LOWER(u.name)
        LIKE LOWER(?)
        ORDER BY b.id DESC;
        """;

        try (
            Connection conn =
            DatabaseConfig.getConnection();

            PreparedStatement stmt =
            conn.prepareStatement(sql)
        ) {

            stmt.setString(
                1,
                "%" + keyword + "%"
            );

            ResultSet rs =
            stmt.executeQuery();

            while (rs.next()) {

                bookings.add(
                    mapResultSetToBooking(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                "Gagal search booking: "
                + e.getMessage()
            );
        }

        return bookings;
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        return new Booking(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("venue_id"),
            rs.getString("event_name"),
            rs.getInt("guest_count"),
            rs.getString("start_date"),
            rs.getString("end_date"),
            rs.getDouble("total_price"),
            BookingStatus.valueOf(rs.getString("status")),
            rs.getString("note")
        );
    }
}