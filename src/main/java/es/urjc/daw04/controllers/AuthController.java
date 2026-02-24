package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.security.Principal;
import org.springframework.security.crypto.password.PasswordEncoder;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginerror() {
        return "errors";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "private";
    }

    @GetMapping("/logout")
    public String logout() {
        return "home";
    }

    @GetMapping("/user")
    public String user(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent,
            Principal principal) {

        String userName = principal.getName();
        User user = userRepository.findByName(userName).orElse(null);

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));

        return "user";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam(name = "confirm-password") String confirmPassword,
            Model model) {

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "login";
        }

        // Validar que el usuario no exista
        if (userRepository.findByName(username).isPresent()) {
            model.addAttribute("error", "El usuario ya existe");
            return "login";
        }

        // Crear nuevo usuario
        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        newUser.setFullName(name);
        newUser.setEncodedPassword(passwordEncoder.encode(password));
        newUser.setRoles(java.util.List.of("USER"));

        userRepository.save(newUser);

        return "redirect:/login";
    }
}
