package com.kosdiam.epoweredmove.repositories.interfaces;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.PlugDto;

@Repository
public interface IPlugRepository {
    PlugDto create(PlugDto plug);
    PlugDto get(String id);
    List<PlugDto> getAll();
    PlugDto update(PlugDto plug);
    Boolean delete(String id);
    List<PlugDto> getAllByChargingStation(String chargingStationId);
    List<PlugDto> getAllByPlugType(String plugTypeId);
}
