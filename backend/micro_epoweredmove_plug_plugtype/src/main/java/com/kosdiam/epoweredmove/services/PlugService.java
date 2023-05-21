package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.models.dtos.PlugDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugRepository;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugTypeRepository;

@Service
public class PlugService {
    Logger logger = Logger.getLogger(PlugService.class.getName());
    @Autowired
    private final IPlugRepository plugRepository;
    @Autowired
    private final IPlugTypeRepository plugTypeRepository;

    public PlugService(IPlugRepository plugRepository, IPlugTypeRepository plugTypeRepository) {
        this.plugRepository = plugRepository;
        this.plugTypeRepository = plugTypeRepository;
    }

    public PlugDto createPlug(PlugDto plug){
        if(plug == null || plug.getPlugTypeId() == null){
            logger.log(Level.WARNING, "Plug Object or plug type id is null");
            return null;
        }
        var createdPlug = plugRepository.create(plug);
        if(createdPlug == null){
            return null;
        }
        return getPlug(createdPlug.getId());
    }

    public PlugDto getPlug(String plugId){
        if(plugId == null) {
            logger.log(Level.WARNING, "Plug id is null");
            return null;
        }
        var completePlug = new PlugDto();

        completePlug = plugRepository.get(plugId);
        if(completePlug == null){
            logger.log(Level.WARNING, "Plug not found");
            return null;
        }

        var plugTypeFound = plugTypeRepository.get(completePlug.getPlugTypeId());
        if(plugTypeFound == null){
            logger.log(Level.WARNING, "Plug type not found");
            return null;
        }
        completePlug.setPlugTypeObj(plugTypeFound);

        return completePlug;
    }

    public List<PlugDto> getPlugs(){
        var plugsFound = plugRepository.getAll();

        if(plugsFound == null){
            return null;
        }
        var allPlugs = new ArrayList<PlugDto>();
        for(var currentPlug : plugsFound){
            var currentCompletePlug = getPlug(currentPlug.getId());
            allPlugs.add(currentCompletePlug);
        }
        return allPlugs;
    }
    public List<PlugDto> getPlugsByChargingStation(String chargingStationId) {
        if(chargingStationId == null){
            return null;
        }
        var allPlugs = new ArrayList<PlugDto>();

        var plugsFound = plugRepository.getAllByChargingStation(chargingStationId);
        if(plugsFound == null){
            return null;
        }
        for(var currentPlug : plugsFound){
            var currentCompletePlug = getPlug(currentPlug.getId());
            if(currentCompletePlug != null){
                allPlugs.add(currentCompletePlug);
            }
        }

        return allPlugs;
    }
    
    
    public List<PlugDto> getPlugsByPlugType(String plugTypeId) {
        if(plugTypeId == null){
            return null;
        }
        var allPlugs = new ArrayList<PlugDto>();

        var plugsFound = plugRepository.getAllByPlugType(plugTypeId);
        if(plugsFound == null){
            return null;
        }
        for(var currentPlug : plugsFound){
            var currentCompletePlug = getPlug(currentPlug.getId());
            if(currentCompletePlug != null){
                allPlugs.add(currentCompletePlug);
            }
        }

        return allPlugs;
    }

    public PlugDto updatePlug(PlugDto editedPlug){
        var currentPlug = getPlug(editedPlug.getId());
        if(currentPlug == null){
            logger.log(Level.WARNING, "Plug not found");
            return null;
        }

        var updatedPlug = plugRepository.update(editedPlug);
        if(updatedPlug == null){
            logger.log(Level.WARNING, "Plug not updated");
            return null;
        }
        return currentPlug;
    }

    public Boolean deletePlug(String plugId){
        if(plugId == null){
            return false;
        }
        var completePlug = getPlug(plugId);
        if(completePlug == null){
            logger.log(Level.WARNING, "Plug not found");
            return false;
        }

        return plugRepository.delete(plugId);
    }
}
