package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.CartService;
import org.springframework.web.bind.annotation.CookieValue;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent) {

        model.addAttribute("products", productService.findAll());
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));

        return "home";
    }
}