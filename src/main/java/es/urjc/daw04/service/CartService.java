package es.urjc.daw04.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Product;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    // --- PRIVATE HELPER METHODS ---

    // Decodes: "1:2_5:1" -> {1=2, 5=1}
    private Map<Long, Integer> decodeCookie(String content) {
        Map<Long, Integer> map = new HashMap<>();
        if (content == null || content.isEmpty())
            return map;

        try {
            // Split by "_" to avoid invalid characters like commas
            String[] items = content.split("_");
            for (String item : items) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    map.put(Long.parseLong(parts[0]), Integer.parseInt(parts[1]));
                }
            }
        } catch (Exception e) {
            // If the cookie is malformed (e.g. manually edited by the user),
            // return whatever we managed to parse, or an empty map.
        }
        return map;
    }

    // Encodes: {1=2, 5=1} -> "1:2_5:1"
    private String encodeCookie(Map<Long, Integer> map) {
        if (map == null || map.isEmpty()) return "";
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            pairs.add(entry.getKey() + ":" + entry.getValue());
        }
        return String.join("_", pairs);
    }

    // --- PUBLIC METHODS ---

    public String addProduct(String currentContent, Long productId) {
        Map<Long, Integer> map = decodeCookie(currentContent);
        map.put(productId, map.getOrDefault(productId, 0) + 1);
        return encodeCookie(map);
    }

    public String removeProduct(String currentContent, Long productId) {
        Map<Long, Integer> map = decodeCookie(currentContent);
        if (map.containsKey(productId)) {
            int newQty = map.get(productId) - 1;
            if (newQty <= 0)
                map.remove(productId);
            else
                map.put(productId, newQty);
        }
        return encodeCookie(map);
    }

    public Cart getCartFromCookie(String content) {
        Cart cart = new Cart(); // Create a new empty cart for EACH request
        Map<Long, Integer> map = decodeCookie(content);

        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null) {
                // Add items to the temporary cart
                cart.addItem(new CartItem(product, entry.getValue()));
            }
        }
        return cart;
    }
}