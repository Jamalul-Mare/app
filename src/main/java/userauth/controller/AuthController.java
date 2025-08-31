// userauth/controller/AuthController.java
package userauth.controller;

import userauth.dto.UserDTO;
import userauth.dto.LoginResponse;
import userauth.user.User;
import userauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    @PostMapping("/api1/register")
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }

    @PostMapping("/api1/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserDTO userDTO) {
        return userService.authenticate(userDTO)
                .map(u -> ResponseEntity.ok(new LoginResponse(u.getId(), u.getUsername())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
