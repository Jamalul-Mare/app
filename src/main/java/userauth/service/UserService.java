// userauth/service/UserService.java
package userauth.service;

import userauth.dto.UserDTO;
import userauth.user.User;
import userauth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    public boolean login(UserDTO userDTO) {
        return authenticate(userDTO).isPresent();
    }

    /** Returns the user iff credentials are valid. */
    public Optional<User> authenticate(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.getUsername())
                .filter(u -> passwordEncoder.matches(userDTO.getPassword(), u.getPassword()));
    }
}