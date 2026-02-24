package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import es.urjc.daw04.service.CartService;

@Controller
public class AdminController {

    @Autowired
    private CartService cartService;

    @GetMapping("/admin")
    public String admin(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent) {
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));
        return "admin";
    }

}
