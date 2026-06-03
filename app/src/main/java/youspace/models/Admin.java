    package youspace.models;

    import youspace.enums.UserRole;
    import youspace.enums.UserStatus;

    public class Admin extends AppUser {

        public Admin() {
            setRole(UserRole.ADMIN);
            setStatus(UserStatus.ACTIVE);
        }

        public Admin(
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
            UserRole.ADMIN,
            status
        );
    }

        @Override
        public String getDashboardName() {
            return "Dashboard Admin / Penyedia Venue";
        }
    }