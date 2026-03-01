package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.urjc.daw04.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;


@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        // Cart desde la cookie
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

        // Rol admin para el header
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

        // Usuario autenticado para el header
        model.addAttribute("isLogged", request.getUserPrincipal() != null);

        // CSRF Token
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
        }
    }
}
