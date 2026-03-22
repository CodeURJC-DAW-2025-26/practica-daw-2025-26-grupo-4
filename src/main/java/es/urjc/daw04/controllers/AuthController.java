package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CookieValue;

import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.UserRepository;
import es.urjc.daw04.security.RepositoryUserDetailsService;
import es.urjc.daw04.service.AuthRegistrationService;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.EmailService;
import jakarta.transaction.Transactional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthRegistrationService authRegistrationService;

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) String register) {
        if (register != null) {
            model.addAttribute("registerMode", true);
        }
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "private";
    }

    @GetMapping("/user")
    public String user(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent,
            Principal principal, HttpServletRequest request) {

        User user = resolveCurrentUser(principal);

        if (user != null) {
            model.addAttribute("userName", user.getName() != null ? user.getName() : "Establecer nombre de usuario");
            model.addAttribute("email", user.getEmail());
            model.addAttribute("fullName",
                    user.getFullName() != null ? user.getFullName() : "Establecer nombre completo");
            model.addAttribute("birthDate", user.getBirthDate() != null ? user.getBirthDate() : "");
            model.addAttribute("shippingAddress", user.getShippingAddress());
        }

        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));

        // Add CSRF token explicitly
        org.springframework.security.web.csrf.CsrfToken csrf = (org.springframework.security.web.csrf.CsrfToken) request
                .getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
            System.out.println("CSRF Token: " + csrf.getToken());
        }

        return "user";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam(name = "confirm-password") String confirmPassword,
            Model model, HttpServletRequest request) {
        try {
            authRegistrationService.registerUser(name, username, email, password, confirmPassword);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }

        // Auto-login the user
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Store the security context in the HTTP session
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return "redirect:/";
    }

    @PostMapping("/user/address/save")
    @Transactional
    public String saveAddress(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String additional,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String phone,
            Principal principal) {

        System.out.println("=== SAVE ADDRESS CALLED ===");

        if (principal == null) {
            System.out.println("Principal is null, redirecting to login");
            return "redirect:/login";
        }

        User user = resolveCurrentUser(principal);
        System.out.println("User resolved: " + (user != null ? user.getName() : "null"));

        if (user != null && street != null && !street.isEmpty()) {

            // Build the address as plain text
            StringBuilder addressBuilder = new StringBuilder();
            addressBuilder.append(street);
            if (additional != null && !additional.isEmpty()) {
                addressBuilder.append("\n").append(additional);
            }
            if (city != null && province != null && postalCode != null) {
                addressBuilder.append("\n").append(city).append(", ")
                        .append(province).append(" ")
                        .append(postalCode);
            }
            if (country != null) {
                addressBuilder.append("\n").append(country);
            }
            if (phone != null) {
                addressBuilder.append("\nTeléfono: ").append(phone);
            }

            user.setShippingAddress(addressBuilder.toString());
            userRepository.save(user);
            System.out.println("Address saved successfully");
        } else {
            System.out.println("User is null or street is empty");
        }

        System.out.println("Redirecting to /user");
        return "redirect:/user";
    }

    @Transactional
    @PostMapping("/user/address/delete")
    public String deleteAddress(Principal principal) {
        User user = resolveCurrentUser(principal);

        if (user != null) {
            user.setShippingAddress(null);
            userRepository.save(user);
        }

        return "redirect:/user";
    }

    @PostMapping("/user/password/change")
    @Transactional
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model,
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletRequest request) {

        User user = resolveCurrentUser(principal);

        // Validate that the old password is correct
        if (!passwordEncoder.matches(oldPassword, user.getEncodedPassword())) {
            addUserAttributesToModel(model, user, cartContent, request, "La contraseña antigua es incorrecta");
            return "user";
        }

        // Validate that the new password and confirmation match
        if (!newPassword.equals(confirmPassword)) {
            addUserAttributesToModel(model, user, cartContent, request, "Las nuevas contraseñas no coinciden");
            return "user";
        }

        // Validate that the new password differs from the old one
        if (oldPassword.equals(newPassword)) {
            addUserAttributesToModel(model, user, cartContent, request,
                    "La nueva contraseña debe ser diferente a la antigua");
            return "user";
        }

        // Encrypt and save the new password
        user.setEncodedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Redirect on success
        return "redirect:/user?passwordSuccess=true";
    }

    // Helper method to add user attributes to the model
    private void addUserAttributesToModel(Model model, User user, String cartContent,
            HttpServletRequest request, String errorMessage) {
        model.addAttribute("userName", user.getName() != null ? user.getName() : "Establecer nombre de usuario");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("fullName", user.getFullName() != null ? user.getFullName() : "Establecer nombre completo");
        model.addAttribute("birthDate", user.getBirthDate() != null ? user.getBirthDate() : "");
        model.addAttribute("shippingAddress", user.getShippingAddress());
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));
        model.addAttribute("error", errorMessage);

        org.springframework.security.web.csrf.CsrfToken csrf = (org.springframework.security.web.csrf.CsrfToken) request
                .getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
        }
    }

    @PostMapping("/user/account/save")
    @Transactional
    public String saveAccount(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String birthDate,
            Principal principal,
            Model model,
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = resolveCurrentUser(principal);

        if (user == null) {
            return "redirect:/login";
        }

        // Validate and update username if different
        if (username != null && !username.isEmpty() && !username.equals(user.getName())) {
            // Check if new username is already in use
            if (userRepository.findByName(username).isPresent()) {
                addUserAttributesToModel(model, user, cartContent, request, "El nombre de usuario ya está en uso");
                return "user";
            }
            user.setName(username);
        }

        // Update full name if it doesn't contain "Establecer"
        if (fullName != null && !fullName.isEmpty() && !fullName.startsWith("Establecer")) {
            user.setFullName(fullName);
        }

        // Update birth date if not empty
        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                user.setBirthDate(java.time.LocalDate.parse(birthDate));
            } catch (Exception e) {
                // Ignore if format is invalid
            }
        }

        userRepository.save(user);

        return "redirect:/user";
    }

    private User resolveCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return null;
        }

        String principalName = principal.getName();
        User user = userRepository.findByName(principalName).orElse(null);
        if (user != null) {
            return user;
        }

        try {
            Long userId = Long.parseLong(principalName);
            return userRepository.findById(userId).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
