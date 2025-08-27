package userauth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import userauth.dto.UserDTO;
import userauth.repository.UserRepository;
import userauth.service.UserService;
import userauth.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class UserServiceLoginTest {

    @Test
    void login_true_when_password_matches() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(repo);

        // "DB" user cu parolă HASH-uită
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User u = new User();
        u.setUsername("john");
        u.setPassword(encoder.encode("secret")); // IMPORTANT: bcrypt hash, nu text simplu
        Mockito.when(repo.findByUsername(eq("john"))).thenReturn(Optional.of(u));

        // credențiale corecte
        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setPassword("secret");

        assertTrue(service.login(dto));
    }

    @Test
    void login_false_when_user_missing() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(repo);

        Mockito.when(repo.findByUsername(eq("ghost"))).thenReturn(Optional.empty());

        UserDTO dto = new UserDTO();
        dto.setUsername("ghost");
        dto.setPassword("whatever");

        assertFalse(service.login(dto));
    }

    @Test
    void login_false_when_password_mismatch() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(repo);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User u = new User();
        u.setUsername("john");
        u.setPassword(encoder.encode("secret")); // hash pt. "secret"
        Mockito.when(repo.findByUsername(eq("john"))).thenReturn(Optional.of(u));

        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setPassword("bad"); // greșită

        assertFalse(service.login(dto));
    }
}