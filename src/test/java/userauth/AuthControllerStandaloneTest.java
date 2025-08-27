package userauth;

import userauth.controller.AuthController;
import userauth.dto.UserDTO;
import userauth.service.UserService;
import userauth.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// pentru handlerul de excepții
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

class AuthControllerStandaloneTest {

    private MockMvc mockMvc;

    // === Exception handler ca să mapeze RuntimeException -> 500 ===
    @ControllerAdvice
    static class GlobalExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handle(RuntimeException ex) {
            // dacă vrei 409 Conflict, schimbă HttpStatus.INTERNAL_SERVER_ERROR cu HttpStatus.CONFLICT
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // === Fake UserService (fără DB/Repo/Mockito) ===
    static class FakeUserService extends UserService {
        private final Map<String, User> store = new HashMap<>();
        FakeUserService() { super(null); }

        @Override
        public User register(UserDTO dto) {
            if (dto.getUsername() == null || dto.getPassword() == null) {
                throw new RuntimeException("invalid");
            }
            if (store.containsKey(dto.getUsername())) {
                throw new RuntimeException("duplicate");
            }
            // simulăm un hash (important e să nu fie plaintext și să semene cu bcrypt)
            User u = new User((long) (store.size() + 1), dto.getUsername(), "$2b$hash");
            store.put(u.getUsername(), u);
            return u;
        }

        @Override
        public boolean login(UserDTO dto) {
            User u = store.get(dto.getUsername());
            return u != null && "$2b$hash".equals(u.getPassword());
        }
    }

    @BeforeEach
    void setup() {
        AuthController controller = new AuthController(new FakeUserService());
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()) // <- important pentru 500
                .build();
    }

    @Test
    void register_success_returns_user_with_hashed_password() throws Exception {
        String payload = "{\"username\":\"alice\",\"password\":\"secret123\"}";

        String json = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // sanity checks: nu expunem plaintext, arată ca bcrypt
        assertThat(json).doesNotContain("secret123");
        assertThat(json).contains("$2");
    }

    @Test
    void register_duplicate_username_returns_5xx() throws Exception {
        String payload = "{\"username\":\"bob\",\"password\":\"p@ss\"}";

        // prima înregistrare -> OK
        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        ).andExpect(status().isOk());

        // a doua cu același username -> RuntimeException => 500 via handler
        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        ).andExpect(status().is5xxServerError());
    }

    @Test
    void login_success_after_register_returns_200_and_message() throws Exception {
        // seed: înregistrează userul
        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"carol\",\"password\":\"Strong1!\"}")
        ).andExpect(status().isOk());

        // login cu aceleași credențiale
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"carol\",\"password\":\"Strong1!\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    void login_nonexistent_user_returns_401() throws Exception {
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"ghost\",\"password\":\"whatever\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void register_missing_username_returns_5xx() throws Exception {
        // lipsește username; FakeUserService aruncă "invalid" -> handler mapează 500
        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"x\"}")
        ).andExpect(status().is5xxServerError());
    }

    @Test
    void register_returns_expected_json_fields() throws Exception {
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"diana\",\"password\":\"s3cret\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("diana"))
                .andExpect(jsonPath("$.password").exists());
    }

}

