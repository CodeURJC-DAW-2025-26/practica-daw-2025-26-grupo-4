package es.urjc.daw04.controllers.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public Cart getCart(Principal principal) {
        User user = resolveUser(principal);
        return cartService.getUserCart(user);
    }

    @PostMapping("/items/{productId}")
    public Cart addItem(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Principal principal) {
        User user = resolveUser(principal);

        for (int i = 0; i < quantity; i++) {
            cartService.addProductToUserCart(user, productId);
        }

        return cartService.getUserCart(user);
    }

    @DeleteMapping("/items/{productId}")
    public Cart removeItem(@PathVariable Long productId, Principal principal) {
        User user = resolveUser(principal);

        cartService.removeProductFromUserCart(user, productId);
        return cartService.getUserCart(user);
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal) {
        User user = resolveUser(principal);

        cartService.clearUserCart(user);
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkout(Principal principal) {
        User user = resolveUser(principal);

        Cart cart = cartService.getUserCart(user);
        if (!cart.isHasItems()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        orderService.saveOrderFromCart(cart, user);
        cartService.clearUserCart(user);
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(principal.getName());
        return userService.findById(userId).orElseThrow();
    }
}
