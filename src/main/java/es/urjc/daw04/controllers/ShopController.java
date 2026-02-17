package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.CartService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{id}")
    public String viewProduct(Model model, @PathVariable Long id) {
        Product p = productService.findById(id).orElse(null);

        if (p != null) {
            model.addAttribute("product", p);
            return "product";
        } else {
        }
        return "redirect:/";
    }

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam long productId) {
        Product p = productService.findById(productId).orElse(null);
        if (p != null) {
            cartService.addProductToCart(p);
        }
        return "redirect:/";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}
