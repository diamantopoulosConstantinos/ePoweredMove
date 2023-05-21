package com.kosdiam.epoweredmove.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;
import com.kosdiam.epoweredmove.models.dtos.ImageResponseDto;
import com.kosdiam.epoweredmove.services.ChargingStationService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

@RestController
@RequestMapping("epoweredmove/chargingStation")
public class ChargingStationController {
    private final ChargingStationService chargingStationService;

    public ChargingStationController(ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }
    
    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ChargingStationDto> getChargingStation(@RequestParam String id) {
        var chargingStation = chargingStationService.getChargingStation(id);
        if(chargingStation == null) {
            throw new RecordNotFoundException("ChargingStation not exist");
        }
        return new ResponseEntity<>(chargingStation, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<ChargingStationDto>> getChargingStations() {
        var chargingStations = chargingStationService.getChargingStations();
        if(chargingStations == null) {
            throw new RecordNotFoundException("ChargingStation error occurred");
        }
        return new ResponseEntity<>(chargingStations, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChargingStationDto> createChargingStation(@RequestBody ChargingStationDto chargingStationRequest) {
        var chargingStation = chargingStationService.createChargingStation(chargingStationRequest);
        if(chargingStation == null) {
            throw new RecordNotFoundException("ChargingStation not created");
        }
        return new ResponseEntity<>(chargingStation, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChargingStationDto> updateChargingStation(@RequestParam String id, @RequestBody ChargingStationDto chargingStationRequest) {
        var chargingStation = chargingStationService.updateChargingStation(chargingStationRequest);
        if(chargingStation == null) {
            throw new RecordNotFoundException("ChargingStation not exist");
        }
        return new ResponseEntity<>(chargingStation, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteChargingStation(@RequestParam String id) {
        var chargingStationDeleted = chargingStationService.deleteChargingStation(id);
        if(!chargingStationDeleted) {
            throw new RecordNotFoundException("ChargingStation not exist");
        }
        return new ResponseEntity<>(chargingStationDeleted, HttpStatus.OK);
    }

    @RequestMapping(path = "addImage", method = RequestMethod.POST)
    public ResponseEntity<ImageResponseDto> addChargingStationImage(@RequestParam() String id, @ModelAttribute() MultipartFile file) {
        var response = chargingStationService.addChargingStationImage(id, file);
        if(response == null) {
            throw new RecordNotFoundException("ChargingStation image not created");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "deleteImage", method = RequestMethod.DELETE)
    public ResponseEntity<ImageResponseDto> deleteChargingStationImage(@RequestParam() String id) {
        var response = chargingStationService.deleteChargingStationImage(id);
        if(response == null) {
            throw new RecordNotFoundException("ChargingStation image not deleted");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
