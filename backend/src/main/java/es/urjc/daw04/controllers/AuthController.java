package es.urjc.daw04.controllers;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CookieValue;

import es.urjc.daw04.model.User;
import es.urjc.daw04.security.RepositoryUserDetailsService;
import es.urjc.daw04.service.AuthRegistrationService;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.UserAccountService;
import jakarta.transaction.Transactional;

@Controller
public class AuthController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private CartService cartService;

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

        User user = userAccountService.findCurrentUser(principal);

        if (user != null) {
            model.addAttribute("userName", user.getName() != null ? user.getName() : "Establecer nombre de usuario");
            model.addAttribute("email", user.getEmail());
            model.addAttribute("fullName",
                    user.getFullName() != null ? user.getFullName() : "Establecer nombre completo");
            model.addAttribute("birthDate", user.getBirthDate() != null ? user.getBirthDate() : "");
            model.addAttribute("shippingAddress", user.getShippingAddress());
            if (user.getProfileImage() != null) {
                model.addAttribute("profileImageUrl", user.getProfileImage().getUrl());
            }
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

        try {
            userAccountService.saveAddress(principal, street, additional, city, province, postalCode, country, phone, false);
            System.out.println("Address saved successfully");
        } catch (IllegalArgumentException ex) {
            if (isUnauthenticated(ex.getMessage())) {
                return "redirect:/login";
            }
            System.out.println("User is null or street is empty");
        }

        System.out.println("Redirecting to /user");
        return "redirect:/user";
    }

    @Transactional
    @PostMapping("/user/address/delete")
    public String deleteAddress(Principal principal) {
        try {
            userAccountService.deleteAddress(principal);
        } catch (IllegalArgumentException ex) {
            if (isUnauthenticated(ex.getMessage())) {
                return "redirect:/login";
            }
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

        try {
            userAccountService.changePassword(principal, oldPassword, newPassword, confirmPassword);
        } catch (IllegalArgumentException ex) {
            if (isUnauthenticated(ex.getMessage())) {
                return "redirect:/login";
            }
            User user = userAccountService.findCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }
            addUserAttributesToModel(model, user, cartContent, request, ex.getMessage());
            return "user";
        }

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
        if (user.getProfileImage() != null) {
            model.addAttribute("profileImageUrl", user.getProfileImage().getUrl());
        }
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

        try {
            userAccountService.updateAccount(principal, username, fullName, birthDate, false);
        } catch (IllegalArgumentException ex) {
            if (isUnauthenticated(ex.getMessage())) {
                return "redirect:/login";
            }
            User user = userAccountService.findCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }
            addUserAttributesToModel(model, user, cartContent, request, ex.getMessage());
            return "user";
        }

        return "redirect:/user";
    }

    private boolean isUnauthenticated(String message) {
        return "Usuario no autenticado".equals(message);
    }

    @PostMapping("/user/profile-image/save")
    @Transactional
    public String saveProfileImage(
            @RequestParam(required = false) MultipartFile profileImage,
            Principal principal,
            Model model,
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            userAccountService.updateProfileImage(principal, profileImage);
        } catch (IllegalArgumentException ex) {
            if (isUnauthenticated(ex.getMessage())) {
                return "redirect:/login";
            }
            User user = userAccountService.findCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }
            addUserAttributesToModel(model, user, cartContent, request, ex.getMessage());
            return "user";
        } catch (IOException ex) {
            User user = userAccountService.findCurrentUser(principal);
            if (user == null) {
                return "redirect:/login";
            }
            addUserAttributesToModel(model, user, cartContent, request, "Error al guardar la imagen");
            return "user";
        }

        return "redirect:/user";
    }

}
