package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.POIDto;

@Repository
public interface IPOIRepository {
    POIDto create(POIDto poi);
    POIDto get(String id);
    POIDto getByChargingStation(String chargingStationId);
    List<POIDto> getAll();
    POIDto update(POIDto poi);
    Boolean delete(String id);
    List<POIDto> getAllByUser(String userId);
}
