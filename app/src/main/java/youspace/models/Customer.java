package youspace.models;

import youspace.enums.UserRole;
import youspace.enums.UserStatus;

public class Customer extends AppUser {

    public Customer() {
        setRole(UserRole.USER);
        setStatus(UserStatus.ACTIVE);
    }

    public Customer(
        int id,
        String name,
        String email,
        String password,
        String phone,
        UserStatus status
    ) {

    super(
        id,
        name,
        email,
        password,
        phone,
        UserRole.USER,
        status    
    );
}

    @Override
    public String getDashboardName() {
        return "Dashboard User / Penyewa";
    }
}