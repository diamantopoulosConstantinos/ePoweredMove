package com.kosdiam.epoweredmove.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;
import com.kosdiam.epoweredmove.models.dtos.ImageResponseDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IChargingStationRepository;

@Service
public class ChargingStationService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private final IChargingStationRepository chargingStationRepository;

    public ChargingStationService(IChargingStationRepository chargingStationRepository) {
        this.chargingStationRepository = chargingStationRepository;
    }

    public ChargingStationDto createChargingStation(ChargingStationDto chargingStation){
        return chargingStationRepository.create(chargingStation);
    }

    public ChargingStationDto getChargingStation(String chargingStationId){
        return chargingStationRepository.get(chargingStationId);
    }

    public List<ChargingStationDto> getChargingStations(){
        return chargingStationRepository.getAll();
    }

    public ChargingStationDto updateChargingStation(ChargingStationDto editedChargingStation){
        return chargingStationRepository.update(editedChargingStation);
    }

    public Boolean deleteChargingStation(String chargingStationId){
        return chargingStationRepository.delete(chargingStationId);
    }

    public ImageResponseDto addChargingStationImage(String chargingStationId, MultipartFile file){
        var response = new ImageResponseDto();

        response.setImageId(chargingStationRepository.addChargingStationImage(file));
        if(response.getImageId() == null){
            return null;
        }
        var chargingStation = getChargingStation(chargingStationId);
        if(chargingStation == null){
            return null;
        }
        //remove previous image
        if(chargingStation.getImageId() != null){
            deleteChargingStationImage(chargingStation.getImageId());
        }

        chargingStation.setImageId(response.getImageId());
        var chargingStationDto = modelMapper.map(chargingStation, ChargingStationDto.class);
        updateChargingStation(chargingStationDto);
        return response;
    }

    public ImageResponseDto deleteChargingStationImage(String id){
        var response = new ImageResponseDto();

        var chargingStation = getChargingStation(id);
        if(chargingStation == null || chargingStation.getImageId() == null){
            response.setIsDeleted(false);
            return response;
        }
        response.setIsDeleted(chargingStationRepository.deleteChargingStationImage(chargingStation.getImageId()));

        if(response.getIsDeleted()){
            chargingStation.setImageId(null);
            var chargingStationDto = modelMapper.map(chargingStation, ChargingStationDto.class);
            updateChargingStation(chargingStationDto);
        }
        return response;
    }
}
