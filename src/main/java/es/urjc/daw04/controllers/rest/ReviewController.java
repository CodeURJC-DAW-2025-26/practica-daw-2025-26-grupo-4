package es.urjc.daw04.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.service.ReviewService;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
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

    //! TODO: 
    //! 1. DTOs
    @GetMapping("/")
    public Collection<Review> getReviews() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        return reviewService.findById(id);
    }
    
    @PostMapping("/")
    public ResponseEntity<Review> postReview(@RequestBody Review review) {
        review = reviewService.save(review);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(review.getId()).toUri();
        return ResponseEntity.created(location).body(review);
    }
    
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable long id, @RequestBody Review review) {
        review = reviewService.update(id, review);
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.delete(id);
    }
}
