package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.feignclient.PlugTypeFeignClient;
import com.kosdiam.epoweredmove.feignclient.UserFeignClient;
import com.kosdiam.epoweredmove.models.dtos.RouteInfoDto;
import com.kosdiam.epoweredmove.models.dtos.VehicleDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IVehicleRepository;
import com.kosdiam.epoweredmove.utils.maps.DistanceService;

@Service
public class VehicleService {
    Logger logger = Logger.getLogger(VehicleService.class.getName());
    @Autowired
    private final IVehicleRepository vehicleRepository;
    
    @Autowired
    @Lazy 
    private UserFeignClient userFeignClient;
    
    @Autowired
    @Lazy 
    private PlugTypeFeignClient plugTypeFeignClient;
    
       
    private final DistanceService distanceService;

    public VehicleService(IVehicleRepository vehicleRepository, UserFeignClient userFeignClient, PlugTypeFeignClient plugTypeFeignClient, DistanceService distanceService) {
        this.vehicleRepository = vehicleRepository;
        this.userFeignClient = userFeignClient;
        this.plugTypeFeignClient = plugTypeFeignClient;
        this.distanceService = distanceService;
    }

    public VehicleDto createVehicle(VehicleDto vehicle){
        if(vehicle == null){
            logger.log(Level.WARNING, "Vehicle Object is null");
            return null;
        }
        var vehicleCreated = vehicleRepository.create(vehicle);
        if(vehicleCreated == null){
            logger.log(Level.WARNING, "Vehicle not created");
            return null;
        }
        return getVehicle(vehicleCreated.getId());
    }

    public VehicleDto getVehicle(String vehicleId){
        if(vehicleId == null){
            logger.log(Level.WARNING, "Vehicle Id is null");
            return null;
        }
        var foundVehicle = vehicleRepository.get(vehicleId);
        if(foundVehicle == null){
            logger.log(Level.WARNING, "Vehicle not found");
            return null;
        }
        var userOwner = userFeignClient.getUser(foundVehicle.getUserId());
        if(userOwner == null){
            logger.log(Level.WARNING, "Vehicle owner not found");
            return null;
        }
        foundVehicle.setUserObj(userOwner);
        var plugType = plugTypeFeignClient.getPlugType(foundVehicle.getPlugTypeId());
        if(plugType == null){
            logger.log(Level.WARNING, "Plug type not found");
            return null;
        }
        foundVehicle.setPlugTypeObj(plugType);
        return foundVehicle;
    }

    public List<VehicleDto> getVehicles(){
        var vehiclesFound = vehicleRepository.getAll();

        if(vehiclesFound == null){
            return null;
        }
        var allVehicles = new ArrayList<VehicleDto>();
        for(var currentVehicle : vehiclesFound){
            var currentCompleteVehicle = getVehicle(currentVehicle.getId());
            allVehicles.add(currentCompleteVehicle);
        }
        return allVehicles;
    }

    public VehicleDto updateVehicle(VehicleDto editedVehicle){
        return vehicleRepository.update(editedVehicle);
    }

    public Boolean deleteVehicle(String vehicleId){
        return vehicleRepository.delete(vehicleId);
    }

    public List<VehicleDto> getVehiclesByUserId(String userId) {
        var vehiclesFound = vehicleRepository.getAllByUserId(userId);

        if(vehiclesFound == null){
            return null;
        }
        var allVehicles = new ArrayList<VehicleDto>();
        for(var currentVehicle : vehiclesFound){
            var currentCompleteVehicle = getVehicle(currentVehicle.getId());
            allVehicles.add(currentCompleteVehicle);
        }
        return allVehicles;
    }

    public RouteInfoDto getRouteInfoByVehicle(String vehicleId, Integer batteryPercentageRemaining, Float currentLatitude, Float currentLongitude, Float destinationLatitude, Float destinationLongitude) {
        if(vehicleId == null || batteryPercentageRemaining == null ||
                currentLatitude == null || currentLongitude == null ||
        destinationLatitude == null || destinationLongitude == null){
            logger.log(Level.WARNING, "some of the parameters are  null");
            return null;
        }

        var vehicleFound = getVehicle(vehicleId);
        if(vehicleFound == null){
            logger.log(Level.WARNING, "vehicle is  null");
            return null;
        }

        var closestPathDistance = getClosestPathDistance(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude);
        if(closestPathDistance == null){
            logger.log(Level.WARNING, "closest path distance is null");
            return null;
        }

        var avgConsumption = vehicleFound.getAvgConsumption();
        var batteryRemainingKwh = (vehicleFound.getUsableBatterySize() * batteryPercentageRemaining) / 100;
        //get the meters an electric vehicle will travel until it runs out of battery
        var maxMetersTravel = ((batteryRemainingKwh*1000) / (avgConsumption)) * 100;
        var metersByVehicle = 0L;
        var metersByFoot = 0L;
        //if battery power is enough for closest path distance
        if(closestPathDistance <= maxMetersTravel){
            metersByVehicle = closestPathDistance;
        }
        else{
            metersByVehicle = Math.round(maxMetersTravel);
            metersByFoot = closestPathDistance - metersByVehicle;
        }
        var reservationRouteInfoDto = new RouteInfoDto();
        reservationRouteInfoDto.setDestinationMeters(closestPathDistance);
        reservationRouteInfoDto.setMetersByVehicle(metersByVehicle);
        reservationRouteInfoDto.setMetersByFoot(metersByFoot);

        return reservationRouteInfoDto;
    }

    private Long getClosestPathDistance(Float latitude1, Float longitude1, Float latitude2, Float longitude2) {
        Long closestDistance = null;
        try{
            var origin = latitude1.toString() + "," + longitude1.toString();
            var destination = latitude2.toString() + "," + longitude2.toString();
            closestDistance = distanceService.getDistanceInMeters(origin, destination);
        }
        catch (Exception e){
            logger.log(Level.WARNING, "Error while getting closest distance of path");
        }
        return closestDistance;
    }
}
