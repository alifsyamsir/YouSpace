package youspace.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import youspace.dao.BookingDAO;
import youspace.dao.VenueDAO;

import youspace.enums.BookingStatus;

import youspace.models.Booking;
import youspace.models.Venue;

import youspace.utils.ValidationUtil;

public class BookingService {

    private final BookingDAO bookingDAO;

    private final VenueDAO venueDAO;

    public BookingService() {

        this.bookingDAO =
                new BookingDAO();

        this.venueDAO =
                new VenueDAO();
    }

    // ======================================================
    // CREATE BOOKING MANUAL
    // ======================================================

    public boolean createBooking(
            int userId,
            int venueId,
            String eventName,
            int guestCount,
            String startDate,
            String endDate,
            String note
    ) {

        // ================= VALIDASI =================

        if (
            ValidationUtil.isEmpty(
                eventName
            )
        ) {

            throw new IllegalArgumentException(
                    "Nama acara wajib diisi."
            );
        }

        if (
            !ValidationUtil.isPositiveNumber(
                guestCount
            )
        ) {

            throw new IllegalArgumentException(
                    "Jumlah tamu harus lebih dari 0."
            );
        }

        if (
            ValidationUtil.isEmpty(startDate)
            ||
            ValidationUtil.isEmpty(endDate)
        ) {

            throw new IllegalArgumentException(
                    "Tanggal mulai dan selesai wajib diisi."
            );
        }

        if (
            startDate.compareTo(endDate) > 0
        ) {

            throw new IllegalArgumentException(
                    "Tanggal mulai tidak boleh setelah tanggal selesai."
            );
        }

        Venue venue =
                venueDAO.findById(
                        venueId
                );

        if (venue == null) {

            throw new IllegalArgumentException(
                    "Venue tidak ditemukan."
            );
        }

        if (!venue.isAvailable()) {

            throw new IllegalArgumentException(
                    "Venue sedang tidak tersedia."
            );
        }

        if (
            !venue.canAccommodate(
                guestCount
            )
        ) {

            throw new IllegalArgumentException(
                    "Jumlah tamu melebihi kapasitas venue."
            );
        }

        // ================= VALIDASI TANGGAL BOOKING =================

        if (
            bookingDAO.isVenueBookedInRange(
                    venueId,
                    startDate,
                    endDate
            )
        ) {

            throw new IllegalArgumentException(
                    "Venue sudah dibooking pada tanggal tersebut."
            );
        }

        // ================= HITUNG TOTAL =================

        long days =
                ChronoUnit.DAYS.between(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                ) + 1;

        double totalPrice =
                days *
                venue.getPricePerDay();

        // ================= CREATE BOOKING =================

        Booking booking =
                new Booking();

        booking.setUserId(userId);

        booking.setVenueId(venueId);

        booking.setEventName(eventName);

        booking.setGuestCount(guestCount);

        booking.setStartDate(startDate);

        booking.setEndDate(endDate);

        booking.setTotalPrice(totalPrice);

        booking.setStatus(
                BookingStatus.WAITING_PAYMENT
        );

        booking.setNote(note);

        return bookingDAO.createBooking(
                booking
        );
    }

    // ======================================================
    // CREATE BOOKING DIRECT
    // ======================================================

    public Booking createBookingDirect(
            int userId,
            int venueId,
            String startDate,
            String endDate,
            double totalPrice
    ) {

        // ================= VALIDASI =================

        if (
            ValidationUtil.isEmpty(startDate)
            ||
            ValidationUtil.isEmpty(endDate)
        ) {

            throw new IllegalArgumentException(
                    "Tanggal booking wajib dipilih."
            );
        }

        if (
            startDate.compareTo(endDate) > 0
        ) {

            throw new IllegalArgumentException(
                    "Tanggal tidak valid."
            );
        }

        Venue venue =
                venueDAO.findById(
                        venueId
                );

        if (venue == null) {

            throw new IllegalArgumentException(
                    "Venue tidak ditemukan."
            );
        }

        // ================= CEK BOOKING =================

        if (
            bookingDAO.isVenueBookedInRange(
                    venueId,
                    startDate,
                    endDate
            )
        ) {

            throw new IllegalArgumentException(
                    "Tanggal sudah dibooking user lain."
            );
        }

        // ================= HITUNG TOTAL OTOMATIS =================

        long days =
                ChronoUnit.DAYS.between(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                ) + 1;

        double finalTotal =
                days *
                venue.getPricePerDay();

        // ================= CREATE BOOKING =================

        Booking booking =
                new Booking();

        booking.setUserId(userId);

        booking.setVenueId(venueId);

        booking.setEventName(
                "Booking Venue"
        );

        booking.setGuestCount(1);

        booking.setStartDate(startDate);

        booking.setEndDate(endDate);

        booking.setTotalPrice(finalTotal);

        booking.setStatus(
                BookingStatus.WAITING_PAYMENT
        );

        booking.setNote("-");

        boolean success =
                bookingDAO.createBooking(
                        booking
                );

        if (!success) {

            throw new IllegalArgumentException(
                    "Gagal membuat booking."
            );
        }

        // ================= AMBIL BOOKING TERBARU =================

        List<Booking> bookings =
                bookingDAO.getBookingsByUser(
                        userId
                );

        if (bookings.isEmpty()) {

            throw new IllegalArgumentException(
                    "Booking gagal dibuat."
            );
        }

        return bookings.get(0);
    }

    // ======================================================
    // USER BOOKINGS
    // ======================================================

    public List<Booking> getBookingsByUser(
            int userId
    ) {

        autoCompleteBookings();

        return bookingDAO.getBookingsByUser(
                userId
        );
    }

    // ======================================================
    // SEARCH BOOKINGS USER NAME
    // ======================================================

    public List<Booking> searchBookingsByUserName(
            String keyword
    ) {

        return bookingDAO.searchBookingsByUserName(
                keyword
        );
    }

    // ======================================================
    // BOOKED DATES
    // ======================================================

    public List<String> getBookedDatesByVenue(
            int venueId
    ) {

        return bookingDAO.getBookedDatesByVenue(
                venueId
        );
    }

    // ======================================================
    // ALL BOOKINGS
    // ======================================================

    public List<Booking> getAllBookings() {

        autoCompleteBookings();

        return bookingDAO.getAllBookings();
    }

    // ======================================================
    // GET BOOKING
    // ======================================================

    public Booking getBookingById(
            int bookingId
    ) {

        return bookingDAO.findById(
                bookingId
        );
    }

    // ======================================================
    // APPROVE BOOKING
    // ======================================================

    public boolean approveBooking(
            int bookingId
    ) {

        return bookingDAO.updateStatus(
                bookingId,
                BookingStatus.APPROVED
        );
    }

    // ======================================================
    // REJECT BOOKING
    // ======================================================

    public boolean rejectBooking(
            int bookingId
    ) {

        return bookingDAO.updateStatus(
                bookingId,
                BookingStatus.REJECTED
        );
    }

    // ======================================================
    // COMPLETE BOOKING
    // ======================================================

    public boolean completeBooking(
            int bookingId
    ) {

        return bookingDAO.updateStatus(
                bookingId,
                BookingStatus.COMPLETED
        );
    }

    // ======================================================
    // AUTO COMPLETE BOOKINGS
    // ======================================================

    public void autoCompleteBookings() {

        bookingDAO.autoCompleteBookings();
    }
}