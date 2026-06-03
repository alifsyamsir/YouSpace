package youspace.models;

import youspace.enums.UserRole;
import youspace.enums.UserStatus;

public abstract class AppUser {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;

    private UserRole role;
    private UserStatus status;

    public AppUser() {
    }

    public AppUser(
            int id,
            String name,
            String email,
            String password,
            String phone,
            UserRole role,
            UserStatus status
    ) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;

        this.role = role;
        this.status = status;
    }

    public abstract String getDashboardName();

    public boolean canLogin() {
        return status == UserStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {

        if (id < 0) {
            throw new IllegalArgumentException(
                    "ID tidak boleh negatif."
            );
        }

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Nama tidak boleh kosong."
            );
        }

        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        if (email == null || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email tidak boleh kosong."
            );
        }

        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        if (password == null || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Password tidak boleh kosong."
            );
        }

        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

        if (phone == null || phone.isBlank()) {

            throw new IllegalArgumentException(
                    "Nomor HP tidak boleh kosong."
            );
        }

        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

}