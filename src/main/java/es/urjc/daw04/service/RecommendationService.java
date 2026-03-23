package es.urjc.daw04.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.RecommendationPack;
import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.CartItemRepository;
import es.urjc.daw04.repositories.OrderRepository;
import es.urjc.daw04.repositories.ProductRepository;

@Service
public class RecommendationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    /**
     * Returns personalized recommendations for the given user.
     *
     * Algorithm:
     *  1. Collect every product the user has bought, building tag and category frequency maps.
     *  2. Score every un-bought product: +3 per shared tag occurrence, +2×freq per category match.
     *  3. Take the top {@code limit} products by score.
     *  4. Group them into packs: one cross-category combo (2-3 products) + individual cards.
     *
     * Falls back to {@link #getBestsellers(int)} when the user has no order history
     * or no un-bought products match any scoring criteria.
     */
    public List<RecommendationPack> getRecommendations(User user, int limit) {
        List<Order> orders = orderRepository.findByUser(user);
        if (orders.isEmpty()) {
            return getBestsellers(limit);
        }

        // --- Step 1: mine frequency information from purchase history ---
        Set<Long> boughtIds = new HashSet<>();
        Map<String, Integer> tagFreq = new HashMap<>();
        Map<Long, Integer> catFreq = new HashMap<>();

        for (Order order : orders) {
            for (CartItem item : order.getItems()) {
                Product p = item.getProduct();
                if (p == null) continue;
                boughtIds.add(p.getId());
                int qty = item.getQuantity();
                if (p.getTags() != null) {
                    for (String tag : p.getTags()) {
                        if (tag != null && !tag.isBlank()) {
                            String normalizedTag = tag.toLowerCase();
                            tagFreq.put(normalizedTag, tagFreq.getOrDefault(normalizedTag, 0) + qty);
                        }
                    }
                }
                if (p.getCategory() != null) {
                    Long categoryId = p.getCategory().getId();
                    if (categoryId != null) {
                        catFreq.put(categoryId, catFreq.getOrDefault(categoryId, 0) + qty);
                    }
                }
            }
        }

        List<Long> preferredCategoryIds = new ArrayList<>(catFreq.keySet());
        List<String> preferredTags = new ArrayList<>(tagFreq.keySet());
        if (preferredCategoryIds.isEmpty() && preferredTags.isEmpty()) {
            return getBestsellers(limit);
        }

        int candidatePoolSize = Math.max(limit * 10, 50);
        List<Product> candidates = boughtIds.isEmpty()
                ? productRepository.findRecommendationCandidatesWithoutExclusions(
                        preferredCategoryIds,
                        preferredTags,
                        PageRequest.of(0, candidatePoolSize))
                : productRepository.findRecommendationCandidates(
                        new ArrayList<>(boughtIds),
                        preferredCategoryIds,
                        preferredTags,
                        PageRequest.of(0, candidatePoolSize));

        // --- Step 2: score un-bought products ---
        Map<Product, Double> scores = new LinkedHashMap<>();
        for (Product p : candidates) {
            if (boughtIds.contains(p.getId())) continue;
            double score = 0;
            if (p.getTags() != null) {
                for (String tag : p.getTags()) {
                    if (tag != null) {
                        score += tagFreq.getOrDefault(tag.toLowerCase(), 0) * 3.0;
                    }
                }
            }
            if (p.getCategory() != null) {
                score += catFreq.getOrDefault(p.getCategory().getId(), 0) * 2.0;
            }
            if (score > 0) {
                scores.put(p, score);
            }
        }

        if (scores.isEmpty()) {
            return getBestsellers(limit);
        }

        // --- Step 3: take top products by score ---
        List<Product> ranked = scores.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        return buildPacks(ranked);
    }

    /**
     * Returns the most purchased products as recommendation packs.
     * Used as fallback for unauthenticated or first-time users.
     */
    public List<RecommendationPack> getBestsellers(int limit) {
        List<Product> products = new ArrayList<>(
                cartItemRepository.findBestsellingProducts(PageRequest.of(0, limit)));

        // Pad with any products if bestsellers list is smaller than requested
        if (products.size() < limit) {
            Set<Long> existing = products.stream()
                    .map(Product::getId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
            int missing = limit - products.size();
            if (existing.isEmpty()) {
                products.addAll(productRepository.findAllByOrderByIdAsc(PageRequest.of(0, missing)).getContent());
            } else {
                products.addAll(productRepository
                        .findByIdNotInOrderByIdAsc(new ArrayList<>(existing), PageRequest.of(0, missing))
                        .getContent());
            }
        }

        return buildPacks(products);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Groups a flat product list into packs.
     *
     * Strategy:
     *  - Build one cross-category "Combo recomendado" with 2-3 products from
     *    the most frequent distinct categories.
     *  - Expose the remaining products as individual single-product packs.
     */
    private List<RecommendationPack> buildPacks(List<Product> products) {
        List<RecommendationPack> result = new ArrayList<>();

        // Group by category id (use -1 for products without category)
        Map<Long, List<Product>> byCategory = new LinkedHashMap<>();
        for (Product p : products) {
            Long catId = -1L;
            if (p.getCategory() != null && p.getCategory().getId() != null) {
                catId = p.getCategory().getId();
            }
            byCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(p);
        }

        // Build one combo from the first 2-3 distinct categories
        List<Long> catIds = new ArrayList<>(byCategory.keySet());
        if (catIds.size() >= 2) {
            List<Product> comboProducts = new ArrayList<>();
            int comboSize = Math.min(3, catIds.size());
            for (int i = 0; i < comboSize; i++) {
                List<Product> catList = byCategory.get(catIds.get(i));
                if (!catList.isEmpty()) {
                    comboProducts.add(catList.remove(0));
                }
            }
            if (comboProducts.size() >= 2) {
                result.add(new RecommendationPack(comboProducts, "Combo recomendado"));
            }
        }

        // Remaining products as individual packs
        for (List<Product> catProducts : byCategory.values()) {
            for (Product p : catProducts) {
                result.add(new RecommendationPack(List.of(p), null));
            }
        }

        return result;
    }
}
