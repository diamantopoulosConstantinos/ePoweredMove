package com.kosdiam.epoweredmove.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosdiam.epoweredmove.models.dtos.RouteInfoDto;
import com.kosdiam.epoweredmove.models.dtos.VehicleDto;
import com.kosdiam.epoweredmove.services.VehicleService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

@RestController
@RequestMapping("epoweredmove/vehicle")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<VehicleDto> getVehicle(@RequestParam String id ) {
        var vehicle = vehicleService.getVehicle(id);
        if(vehicle == null) {
            throw new RecordNotFoundException("Vehicle not exist");
        }
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        var vehicles = vehicleService.getVehicles();
        if(vehicles == null) {
            throw new RecordNotFoundException("Vehicle error occurred");
        }
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @RequestMapping(path = "allByUser", method = RequestMethod.GET)
    public ResponseEntity<List<VehicleDto>> getVehiclesByUserId(@RequestParam String userId) {
        var vehicles = vehicleService.getVehiclesByUserId(userId);
        if(vehicles == null) {
            throw new RecordNotFoundException("Vehicle error occurred");
        }
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @RequestMapping(path = "routeInfoByVehicle", method = RequestMethod.GET)
    public ResponseEntity<RouteInfoDto> getRouteInfoByVehicle(@RequestParam String vehicleId,
                                                              @RequestParam Integer batteryPercentageRemaining,
                                                              @RequestParam Float currentLatitude,
                                                              @RequestParam Float currentLongitude,
                                                              @RequestParam Float destinationLatitude,
                                                              @RequestParam Float destinationLongitude) {
        var vehicleRouteInfo = vehicleService.getRouteInfoByVehicle(
                vehicleId, batteryPercentageRemaining, currentLatitude,
                currentLongitude, destinationLatitude, destinationLongitude);
        if(vehicleRouteInfo == null) {
            throw new RecordNotFoundException("Vehicle error occurred");
        }
        return new ResponseEntity<>(vehicleRouteInfo, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleRequest) {
        var vehicle = vehicleService.createVehicle(vehicleRequest);
        if(vehicle == null) {
            throw new RecordNotFoundException("Vehicle not created");
        }
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDto> updateVehicle(@RequestBody VehicleDto vehicleRequest) {
        var vehicle = vehicleService.updateVehicle(vehicleRequest);
        if(vehicle == null) {
            throw new RecordNotFoundException("Vehicle not exist");
        }
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteVehicle(@RequestParam String id) {
        var vehicleDeleted = vehicleService.deleteVehicle(id);
        if(!vehicleDeleted) {
            throw new RecordNotFoundException("Vehicle not exist");
        }
        return new ResponseEntity<>(vehicleDeleted, HttpStatus.OK);
    }
}
