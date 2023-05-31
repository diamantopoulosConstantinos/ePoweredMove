package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.feignclient.PlugPlugTypeFeignClient;
import com.kosdiam.epoweredmove.feignclient.PoiChargingStationFeignClient;
import com.kosdiam.epoweredmove.feignclient.UserFeignClient;
import com.kosdiam.epoweredmove.feignclient.VehicleFeignClient;
import com.kosdiam.epoweredmove.models.dtos.ReservationDto;
import com.kosdiam.epoweredmove.models.dtos.RouteInfoDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IReservationRepository;
import com.kosdiam.epoweredmove.utils.maps.DistanceService;

@Service
public class ReservationService {
    Logger logger = Logger.getLogger(ReservationService.class.getName());
    @Autowired
    private final IReservationRepository reservationRepository;
    
    
    @Autowired
    @Lazy
    private final PlugPlugTypeFeignClient plugPlugTypeFeignClient;
    
    
    @Autowired
    @Lazy
    private final PoiChargingStationFeignClient poiChargingStationFeignClient;
    
    @Autowired
    @Lazy
    private final VehicleFeignClient vehicleFeignClient;
    
    @Autowired
    @Lazy
    private final UserFeignClient userFeignClient;

    private final DistanceService distanceService;

    public ReservationService(IReservationRepository reservationRepository, PlugPlugTypeFeignClient plugPlugTypeFeignClient,  PoiChargingStationFeignClient poiChargingStationFeignClient, VehicleFeignClient vehicleFeignClient, UserFeignClient userFeignClient, DistanceService distanceService) {
        this.reservationRepository = reservationRepository;
        this.plugPlugTypeFeignClient = plugPlugTypeFeignClient;
        this.poiChargingStationFeignClient = poiChargingStationFeignClient;
        this.vehicleFeignClient = vehicleFeignClient;
        this.userFeignClient = userFeignClient;
        this.distanceService = distanceService;
    }

    public ReservationDto createReservation(ReservationDto reservation){
        var samePlugReservations = getAllByPlug(reservation.getPlugId());
        if(samePlugReservations == null){
            logger.log(Level.WARNING, "Error occurred while fetching reservations");
            return null;
        }

        if(checkIfTimeOverlap(samePlugReservations, reservation.getTimeStart(), reservation.getTimeEnd())){
            logger.log(Level.WARNING, "Reservation time range overlap");
            return null;
        }

        var plugFound = plugPlugTypeFeignClient.getPlug(reservation.getPlugId());
        if(plugFound == null){
            logger.log(Level.WARNING, "No plug found");
            return null;
        }
        var chargingStationFound = poiChargingStationFeignClient.getChargingStation(plugFound.getChargingStationId());
        if(chargingStationFound == null){
            logger.log(Level.WARNING, "No charging station found");
            return null;
        }

        reservation.setAccepted(chargingStationFound.getAutoAccept());

        return reservationRepository.create(reservation);
    }

    public Boolean checkIfTimeOverlap(List<ReservationDto> reservations, Long timeStart, Long timeEnd){
        var timeRangeOverlap = false;
        for(var currentReservation : reservations){
            if(!currentReservation.getCancelled()){
                var start1 = Calendar.getInstance();
                start1.setTimeInMillis(currentReservation.getTimeStart());
                var end1 = Calendar.getInstance();
                end1.setTimeInMillis(currentReservation.getTimeEnd());
                var start2 = Calendar.getInstance();
                start2.setTimeInMillis(timeStart);
                var end2 = Calendar.getInstance();
                end2.setTimeInMillis(timeEnd);

                if (!start1.after(end2) && !start2.after(end1)) {
                    timeRangeOverlap = true;
                    break;
                }
            }
        }
        return timeRangeOverlap;
    }

    public ReservationDto getReservation(String reservationId){
        if(reservationId == null){
            logger.log(Level.WARNING, "Reservation id is null");
            return null;
        }
        var reservationFound = reservationRepository.get(reservationId);
        if(reservationFound == null){
            logger.log(Level.WARNING, "Reservation is null");
            return null;
        }
        var reservedUser = userFeignClient.getUser(reservationFound.getUserId());
        if(reservedUser == null){
            logger.log(Level.WARNING, "User is null");
            return null;
        }
        reservationFound.setUserObj(reservedUser);
        var reservedPlug = plugPlugTypeFeignClient.getPlug(reservationFound.getPlugId());
        if(reservedPlug == null){
            logger.log(Level.WARNING, "Plug is null");
            return null;
        }
        var plugTypeFound = plugPlugTypeFeignClient.getPlugType(reservedPlug.getPlugTypeId());
        if(plugTypeFound == null){
            logger.log(Level.WARNING, "Plug Type is null");
            return null;
        }
        var poiFound = poiChargingStationFeignClient.getPoiByChargingStation(reservedPlug.getChargingStationId());
        if(poiFound == null){
            logger.log(Level.WARNING, "POI is null");
            return null;
        }
        var vehicleFound = vehicleFeignClient.getVehicle(reservationFound.getVehicleId());
        if(vehicleFound == null){
            logger.log(Level.WARNING, "Vehicle is null");
            return null;
        }
        reservedPlug.setPlugTypeObj(plugTypeFound);
        reservationFound.setPlugObj(reservedPlug);
        reservationFound.setVehicleObj(vehicleFound);
        reservationFound.setPoiLatitude(poiFound.getLatitude());
        reservationFound.setPoiLongitude(poiFound.getLongitude());

        return reservationFound;
    }

    public List<ReservationDto> getReservations(){
        var reservationsFound = reservationRepository.getAll();

        if(reservationsFound == null){
            return null;
        }
        var allReservations= new ArrayList<ReservationDto>();
        for(var currentReservation : reservationsFound){
            var currentCompleteReservation = getReservation(currentReservation.getId());
            allReservations.add(currentCompleteReservation);
        }
        return allReservations;
    }

    public List<ReservationDto> getReservationsByChargingStationOwner(String userId) {
        if(userId == null){
            logger.log(Level.WARNING, "User id is null");
            return null;
        }
        var pois = poiChargingStationFeignClient.getPOIsByUser(userId);
        if(pois == null){
            logger.log(Level.WARNING, "POIs is  null");
            return null;
        }
        var plugIds = new ArrayList<String>();
        //get all plug ids of current owner (user id)
        for(var currentPoi : pois){
            var currentPlugs = plugPlugTypeFeignClient.getPlugsByChargingStation(currentPoi.getChargingStationId());
            if(currentPlugs != null){
                for(var currentPlug: currentPlugs){
                    plugIds.add(currentPlug.getId());
                }
            }
        }
        var reservationsByOwner = new ArrayList<ReservationDto>();
        //get all reservations by plug ids
        for(var currentPlugId : plugIds){
            var reservations = getAllByPlug(currentPlugId);
            if(reservations != null){
                reservationsByOwner.addAll(reservations);
            }
        }

        return reservationsByOwner;
    }

    public List<ReservationDto> getReservationsByOwner(String userId) {
        if(userId == null){
            logger.log(Level.WARNING, "User id is null");
            return null;
        }

        var reservationsByOwner = reservationRepository.getAllByOwner(userId);
        if(reservationsByOwner == null){
            logger.log(Level.WARNING, "Reservations found is null");
            return null;
        }
        var completeReservations = new ArrayList<ReservationDto>();
        for(var currentReservation : reservationsByOwner){
            var reservation = getReservation(currentReservation.getId());
            completeReservations.add(reservation);
        }

        return completeReservations;
    }

    public List<ReservationDto> getAllByPlug(String plugId) {
        if(plugId == null){
            logger.log(Level.WARNING, "Plug id is  null");
            return null;
        }
        var reservationsFound = reservationRepository.getAllByPlugId(plugId);
        if(reservationsFound == null){
            logger.log(Level.WARNING, "Reservations is  null");
            return null;
        }
        var completeReservations = new ArrayList<ReservationDto>();
        for(var currentReservation : reservationsFound){
            var completeReservation = getReservation(currentReservation.getId());
            if(completeReservation != null){
                completeReservations.add(completeReservation);
            }
        }
        return completeReservations;
    }

    public ReservationDto updateReservation(ReservationDto editedReservation){
        var reservation = reservationRepository.update(editedReservation);
        if(reservation == null){
            logger.log(Level.WARNING, "Reservation is  null");
            return null;
        }
        return getReservation(editedReservation.getId());
    }

    public Boolean deleteReservation(String reservationId){
        return reservationRepository.delete(reservationId);
    }


    public RouteInfoDto getRouteInfoByReservation(String reservationId, Integer batteryPercentageRemaining, Float currentLatitude, Float currentLongitude) {
        if(reservationId == null || batteryPercentageRemaining == null){
            logger.log(Level.WARNING, "reservation id or battery remaining is  null");
            return null;
        }

        var reservation = getReservation(reservationId);
        if(reservation == null){
            logger.log(Level.WARNING, "Reservation is null");
            return null;
        }

        var poi = poiChargingStationFeignClient.getPoiByChargingStation(reservation.getPlugObj().getChargingStationId());
        if(poi == null){
            logger.log(Level.WARNING, "Poi is null");
            return null;
        }
        var closestPathDistance = getClosestPathDistance(poi.getLatitude(), poi.getLongitude(), currentLatitude, currentLongitude);
        if(closestPathDistance == null){
            logger.log(Level.WARNING, "closest path distance is null");
            return null;
        }
        var remainingBatteryEnergy = reservation.getVehicleObj().getUsableBatterySize() * batteryPercentageRemaining * Math.pow(10, -2);
        var maxMetersTravel = (remainingBatteryEnergy / reservation.getVehicleObj().getAvgConsumption()) * Math.pow(10, 3);
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
