package es.urjc.daw04.controllers.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.urjc.daw04.model.User;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.UserAccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserAccountService userAccountService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        // Cart from cookie
        String cartContent = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    cartContent = cookie.getValue();
                    break;
                }
            }
        }
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));

        // Admin role for header
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

        // Authenticated user for header
        Principal principal = request.getUserPrincipal();
        model.addAttribute("isLogged", principal != null);

        // Profile image for header
        if (principal != null) {
            User user = userAccountService.findCurrentUser(principal);
            if (user != null && user.getProfileImage() != null) {
                model.addAttribute("headerProfileImageUrl", user.getProfileImage().getUrl());
            }
        }

        // CSRF Token
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
        }
    }
}
