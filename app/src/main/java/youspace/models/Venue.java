package youspace.models;

import youspace.enums.VenueCategory;
import youspace.enums.VenueStatus;

public class Venue {

    private int id;
    private String name;
    private String description;
    private VenueCategory category;
    private int capacity;
    private double pricePerDay;
    private String imagePath;
    private VenueStatus status;
    

    public Venue() {
    }

    public Venue(int id, String name, String description, VenueCategory category,
                 int capacity, double pricePerDay, String imagePath, VenueStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.capacity = capacity;
        this.pricePerDay = pricePerDay;
        this.imagePath = imagePath;
        this.status = status;
    }

    public boolean canAccommodate(int guestCount) {
        return guestCount <= capacity;
    }

    public boolean isAvailable() {
        return status == VenueStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID venue tidak boleh negatif.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama venue tidak boleh kosong.");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VenueCategory getCategory() {
        return category;
    }

    public void setCategory(VenueCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Kategori venue tidak boleh kosong.");
        }
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Kapasitas harus lebih dari 0.");
        }
        this.capacity = capacity;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        if (pricePerDay < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
        this.pricePerDay = pricePerDay;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public VenueStatus getStatus() {
        return status;
    }

    public void setStatus(VenueStatus status) {
        this.status = status;
    }
}