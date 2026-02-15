package es.urjc.daw04.services;

import java.util.List;

import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Product;

@Service
public class CartService {
    private Cart cart;

    public CartService() {
        this.cart = new Cart();
    }

    public void addProductToCart(Product product) {
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cart.addItem(new CartItem(product, 1));
    }

    public void decrementProductFromCart(long productId) {
        List<CartItem> items = cart.getItems();

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.getProduct().getId() == productId) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    items.remove(i);
                }
                return;
            }
        }
    }

    public Cart getCart() {
        return cart;
    }
}