package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import es.urjc.daw04.service.CartService;

@Controller
public class AuthController {

    @Autowired
    private CartService cartService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/user")
    public String user(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent) {

        model.addAttribute("userName", "Edu");
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));

        return "user";
    }
}
