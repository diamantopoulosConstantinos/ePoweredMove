package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import com.kosdiam.epoweredmove.feignclient.PoiChargingStationFeignClient;
import com.kosdiam.epoweredmove.feignclient.UserFeignClient;
import com.kosdiam.epoweredmove.models.dtos.ReviewDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IReviewRepository;

@Service
public class ReviewService {
    Logger logger = Logger.getLogger(ReviewService.class.getName());
    @Autowired
    private final IReviewRepository reviewRepository;
    
    @Autowired
    @Lazy
    private final UserFeignClient userFeignClient;
    
    @Autowired
    @Lazy
    private final PoiChargingStationFeignClient chargingStationFeignClient;

    public ReviewService(IReviewRepository reviewRepository, UserFeignClient userFeignClient, PoiChargingStationFeignClient chargingStationFeignClient) {
        this.reviewRepository = reviewRepository;
        this.userFeignClient = userFeignClient;
        this.chargingStationFeignClient = chargingStationFeignClient;
    }

    public ReviewDto createReview(ReviewDto review){
        var createdReview = reviewRepository.create(review);
        if(createdReview == null){
            logger.log(Level.WARNING, "Review id is null");
            return null;
        }
        return getReview(createdReview.getId());
    }

    public ReviewDto getReview(String reviewId){
        if(reviewId == null) {
            logger.log(Level.WARNING, "Review id is null");
            return null;
        }
        var completeReview = new ReviewDto();

        completeReview = reviewRepository.get(reviewId);
        if(completeReview == null){
            logger.log(Level.WARNING, "Review not found");
            return null;
        }
        var chargingStationFound = chargingStationFeignClient.getChargingStation(completeReview.getChargingStationId());
        if(chargingStationFound == null){
            logger.log(Level.WARNING, "Charging station not found");
            return null;
        }
        completeReview.setChargingStationObj(chargingStationFound);
        var userFound = userFeignClient.getUser(completeReview.getUserId());
        if(userFound == null){
            logger.log(Level.WARNING, "User not found");
            return null;
        }
        completeReview.setUserObj(userFound);

        return completeReview;
    }

    public List<ReviewDto> getReviews(){
        var reviewsFound = reviewRepository.getAll();

        if(reviewsFound == null){
            return null;
        }
        var allReviews = new ArrayList<ReviewDto>();
        for(var currentReview : reviewsFound){
            var currentCompleteReview = getReview(currentReview.getId());
            allReviews.add(currentCompleteReview);
        }
        return allReviews;
    }

    public List<ReviewDto> getReviewsByChargingStation(String chargingStationId) {
        if(chargingStationId == null){
            return null;
        }
        var allReviews = new ArrayList<ReviewDto>();

        var reviewsFound = reviewRepository.getAllByChargingStation(chargingStationId);
        if(reviewsFound == null){
            return null;
        }
        for(var currentReview : reviewsFound){
            var currentCompleteReview = getReview(currentReview.getId());
            if(currentCompleteReview != null){
                allReviews.add(currentCompleteReview);
            }
        }

        return allReviews;
    }

    public ReviewDto updateReview(String reviewId, ReviewDto editedReview){
        return reviewRepository.update(reviewId, editedReview);
    }

    public Boolean deleteReview(String reviewId){
        return reviewRepository.delete(reviewId);
    }
}
