package userauth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import userauth.dto.UserDTO;
import userauth.repository.UserRepository;
import userauth.service.UserService;
import userauth.user.User;

@DataJpaTest
@Import(UserService.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void register_hashes_password_and_persists_user() {
        UserDTO dto = new UserDTO();
        dto.setUsername("dana");
        dto.setPassword("MyStrongPass1!");

        User saved = userService.register(dto);
        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("dana", saved.getUsername());
        Assertions.assertNotEquals("MyStrongPass1!", saved.getPassword(), "Password must be hashed!");
        Assertions.assertTrue(saved.getPassword().startsWith("$2"), "BCrypt hash expected.");
    }

    @Test
    void login_returns_true_for_correct_credentials_false_otherwise() {
        UserDTO dto = new UserDTO();
        dto.setUsername("ed");
        dto.setPassword("Top$ecret");
        userService.register(dto);

        // correct
        Assertions.assertTrue(userService.login(dto));

        // wrong password
        UserDTO wrong = new UserDTO();
        wrong.setUsername("ed");
        wrong.setPassword("nope");
        Assertions.assertFalse(userService.login(wrong));

        // unknown user
        UserDTO unknown = new UserDTO();
        unknown.setUsername("ghost");
        unknown.setPassword("123");
        Assertions.assertFalse(userService.login(unknown));
    }

    @Test
    void duplicate_username_throws_runtime_exception() {
        UserDTO a = new UserDTO();
        a.setUsername("ivan");
        a.setPassword("p@ss");
        userService.register(a);

        UserDTO b = new UserDTO();
        b.setUsername("ivan");
        b.setPassword("other");
        Assertions.assertThrows(RuntimeException.class, () -> userService.register(b));
    }
}