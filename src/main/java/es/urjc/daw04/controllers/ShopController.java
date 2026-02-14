package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.services.ProductService;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{id}")
    public String viewProduct(Model model, @PathVariable Long id) {

        Product p = productService.findById(id);

        if (p != null) {
            model.addAttribute("product", p);
            return "product";
        } else {
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}
