package es.urjc.daw04.controllers;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.EnumStatus;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.User;
import es.urjc.daw04.service.CartService;
import es.urjc.daw04.service.OrderService;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.ReviewService;
import es.urjc.daw04.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ShopController {

    private static final int ORDERS_PAGE_SIZE = 5;
    private static final int REVIEWS_PAGE_SIZE = 5;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    // -- PRODUCTS --

    @GetMapping("/product/{id}")
    public String viewProduct(Model model, @PathVariable Long id,
            @RequestParam(defaultValue = "1") int qty, Principal principal,
            HttpServletRequest request) {
        Product p = productService.findById(id).orElse(null);

        if (p != null) {
            model.addAttribute("product", p);

            int quantity = Math.max(1, qty);
            model.addAttribute("quantity", quantity);

            Product recommended = productService.findAll().stream()
                    .filter(anyProduct -> !anyProduct.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            model.addAttribute("recommendedProduct", recommended);

            // Paginated reviews
            Page<Review> firstRevsPage = reviewService.findByProductIdPaged(id, 0, REVIEWS_PAGE_SIZE);
            model.addAttribute("firstReviews", firstRevsPage.getContent());
            model.addAttribute("reviewsHasMore", firstRevsPage.hasNext());
            model.addAttribute("productId", id);

            // Current user's review, if it exists
            if (principal != null) {
                Long userId = Long.parseLong(principal.getName());
                User user = userService.findById(userId).orElse(null);
                if (user != null) {
                    Optional<Review> userReview = reviewService.findByProductIdAndUserId(id, user.getId());
                    userReview.ifPresent(review -> model.addAttribute("userReview", review));
                }
            }

            // Add CSRF token
            org.springframework.security.web.csrf.CsrfToken csrf = (org.springframework.security.web.csrf.CsrfToken) request
                    .getAttribute("_csrf");
            if (csrf != null) {
                model.addAttribute("token", csrf.getToken());
            }

            return "product";
        }
        return "redirect:/";
    }

    @GetMapping("/api/products/{id}/reviews/fragment")
    public String reviewsFragment(@PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        Page<Review> p = reviewService.findByProductIdPaged(id, page, REVIEWS_PAGE_SIZE);
        model.addAttribute("reviews", p.getContent());
        return "fragments/reviews";
    }

    @PostMapping("/product/{id}/review")
    public String addReview(@PathVariable Long id,
            @RequestParam String content,
            @RequestParam double rating,
            HttpServletRequest request) {

        var principal = request.getUserPrincipal();
        Product product = productService.findById(id).orElse(null);

        if (product != null && principal != null) {
            Long userId = Long.parseLong(principal.getName());
            User user = userService.findById(userId).orElse(null);

            if (user != null) {
                // Check if a review already exists for this user and product
                Optional<Review> existingReview = reviewService.findByProductIdAndUserId(id, user.getId());

                if (existingReview.isPresent()) {
                    // Update existing review
                    Review review = existingReview.get();
                    review.setContent(content);
                    review.setRating(rating);
                    reviewService.save(review);
                } else {
                    // Create new review
                    Review review = new Review(product, user, content, rating);
                    reviewService.save(review);
                }
            }
        }

        return "redirect:/product/" + id;
    }

    // -- CART --

    @PostMapping("/review/{reviewId}/edit")
    public String editReview(@PathVariable Long reviewId,
            @RequestParam String content,
            @RequestParam double rating,
            Principal principal) {
        Review review = reviewService.findById(reviewId).orElse(null);


        if (review != null && principal != null) {
            Long userId = Long.parseLong(principal.getName());
            User user = userService.findById(userId).orElse(null);
            if (user != null && review.getUser().getId().equals(user.getId())) {
                review.setContent(content);
                review.setRating(rating);
                reviewService.save(review);
                return "redirect:/product/" + review.getProduct().getId();
            }
        }
        return "redirect:/";
    }

    @PostMapping("/review/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, Principal principal) {
        Review review = reviewService.findById(reviewId).orElse(null);


        if (review != null && principal != null) {
            Long userId = Long.parseLong(principal.getName());
            User user = userService.findById(userId).orElse(null);
            if (user != null && review.getUser().getId().equals(user.getId())) {
                Long productId = review.getProduct().getId();
                reviewService.delete(reviewId);
                return "redirect:/product/" + productId;
            }
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @PostMapping("/cart/add")
    public void addToCart(@RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity,
            @CookieValue(value = "cart", defaultValue = "") String cartContent, HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        String newContent = cartContent;

        for (int i = 0; i < quantity; i++) {
            newContent = cartService.addProduct(newContent, productId);
        }

        Cookie cookie = new Cookie("cart", newContent);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "/");
    }

    @PostMapping("/cart/remove/{id}")
    public void removeFromCart(@PathVariable long id,
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

        String newContent = cartService.removeProduct(cartContent, id);

        Cookie cookie = new Cookie("cart", newContent);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "/cart");
    }

    @PostMapping("/cart/buy")
    public void buyNow(@RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpServletResponse response) throws IOException {


        // Start with empty cart to buy only this item
        String newContent = "";


        // Add the product quantity times
        for (int i = 0; i < quantity; i++) {
            newContent = cartService.addProduct(newContent, productId);
        }

        Cookie cookie = new Cookie("cart", newContent);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        response.sendRedirect("/payment/success");
    }

    // -- PAYMENT --
    @GetMapping("/payment/success")
    public String processPaymentSuccess(
            @CookieValue(value = "cart", defaultValue = "") String cartContent,
            HttpServletResponse response, HttpServletRequest request) {

        var principal = request.getUserPrincipal(); // Logged user check

        if (principal != null && !cartContent.isEmpty()) {
            Long userId = Long.parseLong(principal.getName());
            User user = userService.findById(userId).orElse(null);

            if (user != null) {
                Cart cart = cartService.getCartFromCookie(cartContent);
                orderService.saveOrderFromCart(cart, user);

                // Clear cart cookie
                Cookie cookie = new Cookie("cart", "");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return "redirect:/order";
    }

    // -- ORDER --

    @GetMapping("/order")
    public String order(Model model, HttpServletRequest request, Principal principal) {
        // Add CSRF token
        org.springframework.security.web.csrf.CsrfToken csrf = (org.springframework.security.web.csrf.CsrfToken) request
                .getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("token", csrf.getToken());
        }

        if (principal == null) {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Page<Order> firstPage = orderService.findByUserPaged(user, 0, ORDERS_PAGE_SIZE);
        model.addAttribute("orders", toOrdersData(firstPage.getContent(), principal));
        model.addAttribute("hasMore", firstPage.hasNext());
        return "order";
    }

    @GetMapping("/api/orders/fragment")
    public String ordersFragment(@RequestParam(defaultValue = "1") int page, Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("orders", List.of());
            return "fragments/orders";
        }

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("orders", List.of());
            return "fragments/orders";
        }

        Page<Order> p = orderService.findByUserPaged(user, page, ORDERS_PAGE_SIZE);
        model.addAttribute("orders", toOrdersData(p.getContent(), principal));
        return "fragments/orders";
    }

    @PostMapping("/review/add")
    public String addReview(
            @RequestParam Long productId,
            @RequestParam double rating,
            @RequestParam String content,
            Principal principal) {


        if (principal == null) {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findById(userId).orElse(null);
        Product product = productService.findById(productId).orElse(null);


        if (user != null && product != null) {
            // Check if a review already exists for this user and product
            Optional<Review> existingReview = reviewService.findByProductIdAndUserId(productId, user.getId());


            if (existingReview.isPresent()) {
                // Update existing review
                Review review = existingReview.get();
                review.setContent(content);
                review.setRating(rating);
                reviewService.save(review);
            } else {
                // Create new review
                Review review = new Review(product, user, content, rating);
                reviewService.save(review);
            }
        }


        return "redirect:/order";
    }

    private List<Map<String, Object>> toOrdersData(List<Order> allOrders, Principal principal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy");


        // Obtener el usuario actual
        User currentUser = null;
        if (principal != null) {
            Long userId = Long.parseLong(principal.getName());
            currentUser = userService.findById(userId).orElse(null);
        }
        final User user = currentUser;


        return allOrders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderNumber", "ORD-" + String.format("%04d", order.getId()));
            orderMap.put("orderDate", dateFormat.format(order.getOrderDate()));
            orderMap.put("statusClass", getStatusClass(order.getStatus()));
            orderMap.put("statusText", order.getStatus());
            orderMap.put("total", String.format("%.2f", order.getTotalPrice()));
            orderMap.put("itemCount", order.getItems().size());
            orderMap.put("subtotal", String.format("%.2f", order.getTotalPrice() - order.getShippingCost()));
            orderMap.put("shipping",
                    order.getShippingCost() == 0 ? "Gratis" : String.format("€%.2f", order.getShippingCost()));
            orderMap.put("isCollapsed", true);

            List<Map<String, Object>> itemsData = order.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productId", item.getProduct().getId());
                itemMap.put("imageUrl", item.getProduct().getImages().isEmpty() ? "/images/default"
                        : item.getProduct().getImages().get(0).getUrl());
                itemMap.put("name", item.getProduct().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", String.format("%.2f", item.getProduct().getPrice()));
                itemMap.put("canReview", order.getStatus().equals(EnumStatus.DELIVERED));

                // Check if the user already left a review for this product
                boolean hasReview = false;
                if (user != null) {
                    Optional<Review> existingReview = reviewService.findByProductIdAndUserId(
                            item.getProduct().getId(), user.getId());
                    hasReview = existingReview.isPresent();
                }
                itemMap.put("hasReview", hasReview);


                return itemMap;
            }).collect(Collectors.toList());

            orderMap.put("items", itemsData);
            return orderMap;
        }).collect(Collectors.toList());
    }

    private String getStatusClass(String status) {
        switch (status) {
            case "Entregado":
                return "entregado";
            case "En reparto":
                return "shipping";
            case "En tránsito":
                return "transit";
            default:
                return "pending";
        }
    }
}
