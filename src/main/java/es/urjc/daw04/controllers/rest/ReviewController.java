package es.urjc.daw04.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ReviewDTO;
import es.urjc.daw04.model.mapper.ReviewMapper;
import es.urjc.daw04.service.ReviewService;
import es.urjc.daw04.service.UserService;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/")
    public Collection<ReviewDTO> getReviews() {
        return reviewMapper.toDTOs(reviewService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable long id) {
        Review review = reviewService.findById(id);
        if (review != null) {
            return ResponseEntity.ok(reviewMapper.toDTO(review));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> postReview(@RequestBody ReviewDTO reviewRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(principal.getName());
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Review review = reviewMapper.toDomain(reviewRequest);
        review.setUser(user);
        review = reviewService.save(review);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(review.getId()).toUri();
        return ResponseEntity.created(location).body(reviewMapper.toDTO(review));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable long id, @RequestBody ReviewDTO reviewRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(principal.getName());
        Review existingReview = reviewService.findById(id);
        if (existingReview == null || existingReview.getUser() == null || !existingReview.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update needed fields manually from DTO avoiding security override
        existingReview.setContent(reviewRequest.content());
        existingReview.setRating(reviewRequest.rating());
        
        Review updatedReview = reviewService.update(id, existingReview);
        return ResponseEntity.ok(reviewMapper.toDTO(updatedReview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(principal.getName());
        Review existingReview = reviewService.findById(id);
        if (existingReview == null || existingReview.getUser() == null || !existingReview.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
