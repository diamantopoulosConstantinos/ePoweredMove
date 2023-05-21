package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;

@Repository
public interface IChargingStationRepository {
    ChargingStationDto create(ChargingStationDto chargingStation);
    ChargingStationDto get(String id);
    List<ChargingStationDto> getAll();
    ChargingStationDto update(ChargingStationDto chargingStation);
    Boolean delete(String id);
    String addChargingStationImage(MultipartFile file);
    Boolean deleteChargingStationImage(String id);

}