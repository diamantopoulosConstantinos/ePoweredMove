package com.kosdiam.epoweredmove.repositories.interfaces;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.VehicleDto;

@Repository
public interface IVehicleRepository {
    VehicleDto create(VehicleDto vehicle);
    VehicleDto get(String id);
    List<VehicleDto> getAll();
    List<VehicleDto> getAllByUserId(String userId);
    VehicleDto update(VehicleDto vehicle);
    Boolean delete(String id);
}
