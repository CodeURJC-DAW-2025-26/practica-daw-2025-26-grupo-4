package es.urjc.daw04.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    // --- MÉTODOS AUXILIARES (PRIVADOS) ---

    // Traduce: "1:2_5:1" -> {1=2, 5=1}
    private Map<Long, Integer> decodeCookie(String content) {
        Map<Long, Integer> map = new HashMap<>();
        if (content == null || content.isEmpty())
            return map;

        try {
            // Reemplazamos el separador por "_" para evitar caracteres inválidos como la coma
            String[] items = content.split("_");
            for (String item : items) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    map.put(Long.parseLong(parts[0]), Integer.parseInt(parts[1]));
                }
            }
        } catch (Exception e) {
            // Si la cookie está mal formateada (el usuario la ha editado a mano),
            // devolvemos lo que hayamos podido recuperar o un mapa vacío.
        }
        return map;
    }

    // Traduce: {1=2, 5=1} -> "1:2_5:1"
    private String encodeCookie(Map<Long, Integer> map) {
        if (map == null || map.isEmpty()) return "";
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            pairs.add(entry.getKey() + ":" + entry.getValue());
        }
        return String.join("_", pairs);
    }

    // --- MÉTODOS PÚBLICOS ---

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
        Cart cart = new Cart(); // Creamos un carrito nuevo y vacío para CADA petición
        Map<Long, Integer> map = decodeCookie(content);

        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            productService.findById(entry.getKey()).ifPresent(p -> {
                // Añadimos items al carrito temporal
                cart.addItem(new CartItem(p, entry.getValue()));
            });
        }
        return cart;
    }
}