package es.urjc.daw04.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletResponse;

import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.UserService;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Map<String, Object>> usersData = userService.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("fullName", u.getFullName() != null ? u.getFullName() : "");
            map.put("email", u.getEmail() != null ? u.getEmail() : "");
            map.put("roles", String.join(", ", u.getRoles()));
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("users", usersData);
        return "admin";
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        List<Map<String, Object>> productsData = productService.findAll().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : "-");
            map.put("price", String.format("%.2f", p.getPrice()));
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("products", productsData);
        return "admin-products";
    }

    @PostMapping("/admin/products/{id}/delete")
    public void deleteProduct(@PathVariable Long id, HttpServletResponse response) throws IOException {
        productService.deleteById(id);
        response.sendRedirect("/admin/products");
    }

    @PostMapping("/admin/users/{id}/delete")
    public void deleteUser(@PathVariable Long id, HttpServletResponse response) throws IOException {
        userService.deleteById(id);
        response.sendRedirect("/admin");
    }

}
