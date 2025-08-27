package userauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import userauth.controller.AuthController;
import userauth.dto.UserDTO;
import userauth.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerLoginTest {

    @TestConfiguration
    static class Mocks {
        @Bean UserService userService() { return Mockito.mock(UserService.class); }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserService userService; // mock din @TestConfiguration

    @Test
    void login_ok_returns200() throws Exception {
        Mockito.when(userService.login(Mockito.argThat(d ->
                d != null && "john".equals(d.getUsername()) && "secret".equals(d.getPassword())
        ))).thenReturn(true);

        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setPassword("secret");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void login_badCreds_returns401() throws Exception {
        Mockito.when(userService.login(Mockito.any(UserDTO.class))).thenReturn(false);

        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setPassword("bad");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
