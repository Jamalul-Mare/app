package userauth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        // dacă ai PasswordEncoder în constructor, pasează-l și pe acela aici
        UserService service = new UserService(repo);

        User u = new User();
        u.setUsername("john");
        u.setPassword("secret"); // dacă folosești encoder: pune HASH-ul aici

        Mockito.when(repo.findByUsername(eq("john"))).thenReturn(Optional.of(u));

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
        dto.setPassword("anything");

        assertFalse(service.login(dto));
    }

    @Test
    void login_false_when_password_wrong() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(repo);

        User u = new User();
        u.setUsername("john");
        u.setPassword("secret");

        Mockito.when(repo.findByUsername(eq("john"))).thenReturn(Optional.of(u));

        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setPassword("bad");

        assertFalse(service.login(dto));
    }
}
