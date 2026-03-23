package es.urjc.daw04.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ReviewDTO;
import es.urjc.daw04.model.mapper.ReviewMapper;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.ReviewService;
import es.urjc.daw04.service.UserService;
import es.urjc.daw04.model.Product;

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/")
    public Page<ReviewDTO> getReviews(Pageable pageable) {
        return reviewService.findAll(pageable).map(reviewMapper::toDTO);
    }

    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable long id) {
        Review review = reviewService.findById(id);
        return reviewMapper.toDTO(review);
    }
    
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> postReview(@RequestBody ReviewDTO reviewRequest, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (reviewRequest.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findById(userId).orElseThrow();
        Product product = productService.findById(reviewRequest.productId());
        
        Review review = reviewMapper.toDomain(reviewRequest);
        review.setUser(user);
        review.setProduct(product);
        review = reviewService.save(review);
        ReviewDTO reviewDTO = reviewMapper.toDTO(review);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(reviewDTO.id()).toUri();
        
        return ResponseEntity.created(location).body(reviewDTO);
    }
    
    @PutMapping("/{id}")
    public ReviewDTO updateReview(@PathVariable long id, @RequestBody ReviewDTO reviewRequest, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(principal.getName());
        Review existingReview = reviewService.findById(id);
        if (existingReview.getUser() == null || !existingReview.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Update needed fields manually from DTO avoiding security override
        existingReview.setContent(reviewRequest.content());
        existingReview.setRating(reviewRequest.rating());
        
        Review updatedReview = reviewService.update(id, existingReview);
        return reviewMapper.toDTO(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ReviewDTO deleteReview(@PathVariable long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(principal.getName());
        Review existingReview = reviewService.findById(id);
        if (existingReview.getUser() == null || !existingReview.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        reviewService.delete(id);
        return reviewMapper.toDTO(existingReview);
    }
}
