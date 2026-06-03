package youspace.service;

import java.util.List;

import youspace.dao.VenueDAO;
import youspace.enums.VenueCategory;
import youspace.enums.VenueStatus;
import youspace.models.Venue;
import youspace.utils.ValidationUtil;

public class VenueService {

    private final VenueDAO venueDAO;

    public VenueService() {
        this.venueDAO = new VenueDAO();
    }

    // --- METHOD TAMBAHAN UNTUK REKOMENDASI DASHBOARD ---
    public Venue getFirstVenueByCategory(String categoryName) {
        if (ValidationUtil.isEmpty(categoryName)) {
            throw new IllegalArgumentException("Nama kategori wajib diisi.");
        }
        return venueDAO.getFirstVenueByCategory(categoryName);
    }
    // ----------------------------------------------------

    public boolean addVenue(String name, String description, VenueCategory category,
                            int capacity, double pricePerDay, String imagePath) {

        validateVenueData(name, category, capacity, pricePerDay);

        Venue venue = new Venue();
        venue.setName(name);
        venue.setDescription(description);
        venue.setCategory(category);
        venue.setCapacity(capacity);
        venue.setPricePerDay(pricePerDay);
        venue.setImagePath(imagePath);
        venue.setStatus(VenueStatus.AVAILABLE);

        return venueDAO.addVenue(venue);
    }

    public boolean updateVenue(Venue venue) {
        validateVenueData(
            venue.getName(),
            venue.getCategory(),
            venue.getCapacity(),
            venue.getPricePerDay()
        );

        return venueDAO.updateVenue(venue);
    }

    public boolean deleteVenue(int venueId) {
        if (venueId <= 0) {
            throw new IllegalArgumentException("Venue ID tidak valid.");
        }

        return venueDAO.deleteVenue(venueId);
    }

    public Venue getVenueById(int venueId) {
        if (venueId <= 0) {
            throw new IllegalArgumentException("Venue ID tidak valid.");
        }

        return venueDAO.findById(venueId);
    }

    public List<Venue> getAllVenues() {
        return venueDAO.getAllVenues();
    }

    public List<Venue> getRecommendedVenues() {
        return venueDAO.getRecommendedVenues();
    }

    public List<Venue> getVenuesByCategory(VenueCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Kategori venue wajib dipilih.");
        }

        return venueDAO.getVenuesByCategory(category);
    }

    

    private void validateVenueData(String name, VenueCategory category,
                                   int capacity, double pricePerDay) {

        if (ValidationUtil.isEmpty(name)) {
            throw new IllegalArgumentException("Nama venue wajib diisi.");
        }

        if (category == null) {
            throw new IllegalArgumentException("Kategori venue wajib dipilih.");
        }

        if (!ValidationUtil.isPositiveNumber(capacity)) {
            throw new IllegalArgumentException("Kapasitas harus lebih dari 0.");
        }

        if (!ValidationUtil.isPositivePrice(pricePerDay)) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
    }
}