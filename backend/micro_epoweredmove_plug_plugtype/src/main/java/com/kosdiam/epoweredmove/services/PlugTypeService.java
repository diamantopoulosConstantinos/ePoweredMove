package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosdiam.epoweredmove.models.dtos.ImageResponseDto;
import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugRepository;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugTypeRepository;

@Service
public class PlugTypeService {
    Logger logger = Logger.getLogger(PlugTypeService.class.getName());
    @Autowired
    private final IPlugTypeRepository plugTypeRepository;
    @Autowired
    private final IPlugRepository plugRepository;
    @Autowired
    private ModelMapper modelMapper;

    public PlugTypeService(IPlugTypeRepository plugTypeRepository, IPlugRepository plugRepository) {
        this.plugTypeRepository = plugTypeRepository;
        this.plugRepository = plugRepository;
    }

    public PlugTypeDto createPlugType(PlugTypeDto plugType){
        return plugTypeRepository.create(plugType);
    }

    public PlugTypeDto getPlugType(String plugTypeId){
        return plugTypeRepository.get(plugTypeId);
    }

    public List<PlugTypeDto> getPlugTypes(){
        return plugTypeRepository.getAll();
    }

    public PlugTypeDto updatePlugType(PlugTypeDto editedPlugType){
        return plugTypeRepository.update(editedPlugType);
    }

    public Boolean deletePlugType(String plugTypeId){
        return plugTypeRepository.delete(plugTypeId);
    }
    public ImageResponseDto addPlugTypeImage(String plugTypeId, MultipartFile file){
        var response = new ImageResponseDto();

        response.setImageId(plugTypeRepository.addPlugTypeImage(file));
        if(response.getImageId() == null){
            return null;
        }
        var plugType = getPlugType(plugTypeId);
        if(plugType == null){
            return null;
        }
        plugType.setImageId(response.getImageId());
        var plugTypeDto = modelMapper.map(plugType, PlugTypeDto.class);
        updatePlugType(plugTypeDto);
        return response;
    }

    public ImageResponseDto deletePlugTypeImage(String id){
        var response = new ImageResponseDto();

        var plugType = getPlugType(id);
        if(plugType == null){
            response.setIsDeleted(false);
            return response;
        }
        response.setIsDeleted(plugTypeRepository.deletePlugTypeImage(id));

        if(response.getIsDeleted()){
            plugType.setImageId(null);
            var plugTypeDto = modelMapper.map(plugType, PlugTypeDto.class);
            updatePlugType(plugTypeDto);
        }
        return response;
    }

    public List<PlugTypeDto> getPlugTypesByChargingStation(String chargingStationId) {
        if(chargingStationId == null){
            logger.log(Level.WARNING, "Charging station id is null");
            return null;
        }
        var plugsFound = plugRepository.getAllByChargingStation(chargingStationId);
        if(plugsFound == null){
            logger.log(Level.WARNING, "Plugs found is null");
            return null;
        }
        var plugTypesFound = new ArrayList<PlugTypeDto>();
        for(var plug : plugsFound){
            plugTypesFound.add(getPlugType(plug.getPlugTypeId()));
        }
        return plugTypesFound;
    }
}
