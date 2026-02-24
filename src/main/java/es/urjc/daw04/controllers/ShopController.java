package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{id}")
    public String viewProduct(Model model, @PathVariable Long id, 
                             @RequestParam(defaultValue = "1") int qty,
                             @CookieValue(value = "cart", defaultValue = "") String cartContent) {
        Product p = productService.findById(id).orElse(null);

        if (p != null) {
            model.addAttribute("product", p);
            
            Cart currentCart = cartService.getCartFromCookie(cartContent);
            model.addAttribute("cart", currentCart);
            
            // Mantener la cantidad seleccionada, mínimo 1
            int quantity = Math.max(1, qty);
            model.addAttribute("quantity", quantity);

            Product recommended = productService.findAll().stream()
                    .filter(anyProduct -> !anyProduct.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            model.addAttribute("recommendedProduct", recommended);

            return "product";
        } else {
        }
        return "redirect:/";
    }

    @GetMapping("/product/{id}/increase-qty")
    public String increaseQuantity(@PathVariable Long id, @RequestParam(defaultValue = "1") int current) {
        return "redirect:/product/" + id + "?qty=" + (current + 1);
    }

    @GetMapping("/product/{id}/decrease-qty")
    public String decreaseQuantity(@PathVariable Long id, @RequestParam(defaultValue = "1") int current) {
        int newQty = Math.max(1, current - 1);
        return "redirect:/product/" + id + "?qty=" + newQty;
    }

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent) {
        Cart currentCart = cartService.getCartFromCookie(cartContent);

        model.addAttribute("cart", currentCart);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity,
            @CookieValue(value = "cart", defaultValue = "") String cartContent, HttpServletResponse response,
            HttpServletRequest request) {
        String newContent = cartContent;
        
        // Añadir el producto la cantidad de veces especificada
        for (int i = 0; i < quantity; i++) {
            newContent = cartService.addProduct(newContent, productId);
        }

        Cookie cookie = new Cookie("cart", newContent);

        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7 días (en segundos)

        response.addCookie(cookie);

        String referer = request.getHeader("Referer");

        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable long id,
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletResponse response,
            HttpServletRequest request) {

        String newContent = cartService.removeProduct(cartContent, id);

        Cookie cookie = new Cookie("cart", newContent);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @GetMapping("/order")
    public String order(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent) {
        model.addAttribute("cart", cartService.getCartFromCookie(cartContent));
        return "order";
    }
}
