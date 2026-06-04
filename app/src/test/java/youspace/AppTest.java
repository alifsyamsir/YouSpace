package youspace;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import youspace.database.DatabaseInitializer;
import youspace.dao.UserDAO;
import youspace.models.Customer;
import youspace.models.AppUser;
import youspace.enums.UserRole;
import youspace.enums.UserStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @BeforeAll
    static void setup() {
        DatabaseInitializer.initialize();
    }

    @Test
    void testSearchUsers() {
        UserDAO userDAO = new UserDAO();
        
        // Buat customer baru untuk ditest
        Customer customer = new Customer();
        customer.setName("Budi Utomo");
        customer.setEmail("budi.utomo@test.com");
        customer.setPassword("budi123");
        customer.setPhone("087712345678");
        customer.setRole(UserRole.USER);
        customer.setStatus(UserStatus.ACTIVE);
        
        // Hapus jika sudah ada (idempotent)
        AppUser existing = userDAO.findByEmail("budi.utomo@test.com");
        if (existing == null) {
            boolean registered = userDAO.register(customer);
            assertTrue(registered, "Harus bisa register user baru untuk testing");
        }

        // Jalankan pencarian berdasarkan nama
        List<AppUser> searchName = userDAO.searchUsers("Budi");
        assertFalse(searchName.isEmpty(), "User harus ditemukan berdasarkan nama");
        assertTrue(searchName.stream().anyMatch(u -> u.getEmail().equals("budi.utomo@test.com")));

        // Jalankan pencarian berdasarkan email
        List<AppUser> searchEmail = userDAO.searchUsers("budi.utomo");
        assertFalse(searchEmail.isEmpty(), "User harus ditemukan berdasarkan email");

        // Jalankan pencarian berdasarkan nomor hp
        List<AppUser> searchPhone = userDAO.searchUsers("08771234");
        assertFalse(searchPhone.isEmpty(), "User harus ditemukan berdasarkan nomor hp");
    }
}
