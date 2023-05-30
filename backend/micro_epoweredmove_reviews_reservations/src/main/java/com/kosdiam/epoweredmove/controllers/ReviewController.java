package com.kosdiam.epoweredmove.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosdiam.epoweredmove.models.dtos.ReviewDto;
import com.kosdiam.epoweredmove.services.ReviewService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @RequestMapping(path = "hello", method = RequestMethod.GET)
    @RateLimiter(name = "hello", fallbackMethod = "helloFallback")
    public String hello() {
    	Optional<String> podName = Optional.ofNullable(System.getenv("HOSTNAME"));
		return "Hello, Welcome to K8s cluster = " + podName.get();
    }
    
    private String helloFallback(Throwable t) {
		return "Hello (fallback)";
	}

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ReviewDto> getReview(@RequestParam String id) {
        var review = reviewService.getReview(id);
        if(review == null) {
            throw new RecordNotFoundException("Review not exist");
        }
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<ReviewDto>> getReviews() {
        var reviews = reviewService.getReviews();
        if(reviews == null) {
            throw new RecordNotFoundException("Review error occurred");
        }
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @RequestMapping(path = "allByChargingStation", method = RequestMethod.GET)
    public ResponseEntity<List<ReviewDto>> getReviewsByChargingStation(@RequestParam String chargingStationId) {
        var reviews = reviewService.getReviewsByChargingStation(chargingStationId);
        if(reviews == null) {
            throw new RecordNotFoundException("Reviews error occurred");
        }
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewRequest) {
        var review = reviewService.createReview(reviewRequest);
        if(review == null) {
            throw new RecordNotFoundException("Review not created");
        }
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDto> updateReview(@RequestParam String id, @RequestBody ReviewDto reviewRequest) {
        var review = reviewService.updateReview(id, reviewRequest);
        if(review == null) {
            throw new RecordNotFoundException("Review not exist");
        }
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteChargingStation(@RequestParam String id) {
        var reviewDeleted = reviewService.deleteReview(id);
        if(!reviewDeleted) {
            throw new RecordNotFoundException("Review not exist");
        }
        return new ResponseEntity<>(reviewDeleted, HttpStatus.OK);
    }
}
