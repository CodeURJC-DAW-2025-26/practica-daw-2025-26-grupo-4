package es.urjc.daw04.controllers;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.OrderService;
import es.urjc.daw04.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/product/{id}")
    public String viewProduct(Model model, @PathVariable Long id, 
                             @RequestParam(defaultValue = "1") int qty,
                             @CookieValue(value = "cart", defaultValue = "") String cartContent,
                             HttpServletRequest request) {
        Product p = productService.findById(id).orElse(null);

        if (p != null) {
            // Get CSRF token
            org.springframework.security.web.csrf.CsrfToken csrf = 
                (org.springframework.security.web.csrf.CsrfToken) request.getAttribute("_csrf");
            if (csrf != null) {
                model.addAttribute("token", csrf.getToken());
            }

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

    @PostMapping("/product/{id}/increase-qty")
    public String increaseQuantity(@PathVariable Long id, @RequestParam(defaultValue = "1") int current) {
        return "redirect:/product/" + id + "?qty=" + (current + 1);
    }

    @PostMapping("/product/{id}/decrease-qty")
    public String decreaseQuantity(@PathVariable Long id, @RequestParam(defaultValue = "1") int current) {
        int newQty = Math.max(1, current - 1);
        return "redirect:/product/" + id + "?qty=" + newQty;
    }

    @GetMapping("/cart")
    public String cart(Model model, @CookieValue(value = "cart", defaultValue = "") String cartContent,
                      HttpServletRequest request) {
        // Get CSRF token
        org.springframework.security.web.csrf.CsrfToken csrf = 
            (org.springframework.security.web.csrf.CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
        }

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
        
        // Obtener todas las órdenes
        List<Order> allOrders = orderService.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy");
        
        // Convertir órdenes a formato Mustache
        List<Map<String, Object>> ordersData = allOrders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderNumber", "ORD-" + String.format("%04d", order.getId()));
            orderMap.put("orderDate", dateFormat.format(order.getOrderDate()));
            orderMap.put("statusClass", getStatusClass(order.getStatus()));
            orderMap.put("statusText", order.getStatus());
            orderMap.put("total", String.format("%.2f", order.getTotalPrice()));
            orderMap.put("itemCount", order.getItems().size());
            orderMap.put("subtotal", String.format("%.2f", order.getTotalPrice() - order.getShippingCost()));
            orderMap.put("shipping", order.getShippingCost() == 0 ? "Gratis" : String.format("€%.2f", order.getShippingCost()));
            orderMap.put("isCollapsed", true);
            
            // Convertir items
            List<Map<String, Object>> itemsData = order.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productId", item.getProduct().getId());
                itemMap.put("imageUrl", item.getProduct().getImages().get(0));
                itemMap.put("name", item.getProduct().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", String.format("%.2f", item.getProduct().getPrice()));
                itemMap.put("canReview", order.getStatus().equals("Entregado"));
                return itemMap;
            }).collect(Collectors.toList());
            
            orderMap.put("items", itemsData);
            return orderMap;
        }).collect(Collectors.toList());
        
        model.addAttribute("orders", ordersData);
        return "order";
    }
    
    private String getStatusClass(String status) {
        switch (status) {
            case "Entregado": return "entregado";
            case "En reparto": return "shipping";
            case "En tránsito": return "transit";
            default: return "pending";
        }
    }
}
