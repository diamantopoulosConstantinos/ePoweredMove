package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.ReviewDto;

@Repository
public interface IReviewRepository {
    ReviewDto create(ReviewDto review);
    ReviewDto get(String id);
    List<ReviewDto> getAll();
    ReviewDto update(String id, ReviewDto review);
    Boolean delete(String id);

    List<ReviewDto> getAllByChargingStation(String chargingStationId);
}
