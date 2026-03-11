package es.urjc.daw04.controllers.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.User;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.OrderService;
import es.urjc.daw04.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<Cart> getCart(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(cartService.getUserCart(user));
    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<Cart> addItem(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Principal principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.status(401).build();

        for (int i = 0; i < quantity; i++) {
            cartService.addProductToUserCart(user, productId);
        }

        return ResponseEntity.ok(cartService.getUserCart(user));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItem(@PathVariable Long productId, Principal principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.removeProductFromUserCart(user, productId);
        return ResponseEntity.ok(cartService.getUserCart(user));
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> clearCart(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.clearUserCart(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.status(401).build();

        Cart cart = cartService.getUserCart(user);
        if (!cart.isHasItems()) return ResponseEntity.badRequest().build();

        orderService.saveOrderFromCart(cart, user);
        cartService.clearUserCart(user);
        return ResponseEntity.noContent().build();
    }

    private User resolveUser(Principal principal) {
        if (principal == null) return null;
        Long userId = Long.parseLong(principal.getName());
        return userService.findById(userId).orElse(null);
    }
}
