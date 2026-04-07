package es.urjc.daw04.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.UserRepository;

@Service
public class AuthRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User registerUser(String fullName, String username, String email, String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (userRepository.findByName(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setEncodedPassword(passwordEncoder.encode(password));
        newUser.setRoles(List.of("USER"));

        User savedUser = userRepository.save(newUser);
        emailService.sendWelcomeEmail(email, fullName);

        return savedUser;
    }
}
