package es.urjc.daw04.controllers.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.User;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.ReviewService;
import es.urjc.daw04.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {

    private static final int REVIEWS_PAGE_SIZE = 5;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/products/{id}/reviews/fragment")
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
        Product product = productService.findById(id);

        if (product != null && principal != null) {
            User user = resolveCurrentUser(principal);

            if (user != null) {
                Review existingReview = reviewService.findByProductIdAndUserId(id, user.getId());

                if (existingReview != null) {
                    existingReview.setContent(content);
                    existingReview.setRating(rating);
                    reviewService.save(existingReview);
                } else {
                    Review review = new Review(product, user, content, rating);
                    reviewService.save(review);
                }
            }
        }

        return "redirect:/product/" + id;
    }

    @PostMapping("/review/{reviewId}/edit")
    public String editReview(@PathVariable Long reviewId,
            @RequestParam String content,
            @RequestParam double rating,
            Principal principal) {
        Review review = reviewService.findById(reviewId);

        if (review != null && principal != null) {
            User user = resolveCurrentUser(principal);
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
        Review review = reviewService.findById(reviewId);

        if (review != null && principal != null) {
            User user = resolveCurrentUser(principal);
            if (user != null && review.getUser().getId().equals(user.getId())) {
                Long productId = review.getProduct().getId();
                reviewService.delete(reviewId);
                return "redirect:/product/" + productId;
            }
        }
        return "redirect:/";
    }

    @PostMapping("/review/add")
    public String addReviewFromOrder(
            @RequestParam Long productId,
            @RequestParam double rating,
            @RequestParam String content,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = resolveCurrentUser(principal);
        Product product = productService.findById(productId);

        if (user != null && product != null) {
            Review existingReview = reviewService.findByProductIdAndUserId(productId, user.getId());

            if (existingReview != null) {
                existingReview.setContent(content);
                existingReview.setRating(rating);
                reviewService.save(existingReview);
            } else {
                Review review = new Review(product, user, content, rating);
                reviewService.save(review);
            }
        }

        return "redirect:/order";
    }

    private User resolveCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return null;
        }

        String principalName = principal.getName();
        User user = userService.findByName(principalName).orElse(null);
        if (user != null) {
            return user;
        }

        try {
            Long userId = Long.parseLong(principalName);
            return userService.findById(userId).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
