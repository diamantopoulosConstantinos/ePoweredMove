package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.feignclient.PaymentMethodFeignClient;
import com.kosdiam.epoweredmove.feignclient.PlugFeignClient;
import com.kosdiam.epoweredmove.feignclient.ReservationFeignClient;
import com.kosdiam.epoweredmove.feignclient.UserFeignClient;
import com.kosdiam.epoweredmove.feignclient.VehicleFeignClient;
import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;
import com.kosdiam.epoweredmove.models.dtos.POIDto;
import com.kosdiam.epoweredmove.models.dtos.ReservationDto;
import com.kosdiam.epoweredmove.models.enums.Availability;
import com.kosdiam.epoweredmove.repositories.implementations.ChargingStationRepository;
import com.kosdiam.epoweredmove.repositories.interfaces.IChargingStationRepository;
import com.kosdiam.epoweredmove.repositories.interfaces.IPOIRepository;
import com.kosdiam.epoweredmove.utils.maps.DistanceService;

@Service
public class POIService {
    Logger logger = Logger.getLogger(POIService.class.getName());
    @Autowired
    private final IPOIRepository poiRepository;
    @Autowired
    private final IChargingStationRepository chargingStationRepository;
        
    @Autowired
    @Lazy 
    private final UserFeignClient userFeignClient;
    
    @Autowired
    @Lazy 
    private final PaymentMethodFeignClient paymentMethodFeignClient;
    
    @Autowired
    @Lazy 
    private final VehicleFeignClient vehicleFeignClient;
    
    @Autowired
    @Lazy 
    private final PlugFeignClient plugFeignClient;
    
    @Autowired
    @Lazy 
    private final ReservationFeignClient reservationFeignClient;
    

    private final DistanceService distanceService;

    public POIService(IPOIRepository poiRepository, ChargingStationRepository chargingStationRepository, UserFeignClient userFeignClient, PaymentMethodFeignClient paymentMethodFeignClient, VehicleFeignClient vehicleFeignClient, PlugFeignClient plugFeignClient, ReservationFeignClient reservationFeignClient, DistanceService distanceService) {
        this.poiRepository = poiRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.userFeignClient = userFeignClient;
        this.paymentMethodFeignClient = paymentMethodFeignClient;
        this.vehicleFeignClient = vehicleFeignClient;
        this.plugFeignClient = plugFeignClient;
        this.reservationFeignClient = reservationFeignClient;
        this.distanceService = distanceService;
    }

    public POIDto createPOI(POIDto poi){
        if(poi == null || poi.getChargingStationObj() == null){
            logger.log(Level.WARNING, "Poi or Charging Station Object is null");
            return null;
        }
        var chargingStationCreated = chargingStationRepository.create(poi.getChargingStationObj());
        if(chargingStationCreated == null){
            return null;
        }


        poi.setUserId(poi.getUserObj().getId());
        poi.setChargingStationId(chargingStationCreated.getId());
        var poiCreated = poiRepository.create(poi);
        if(poiCreated == null){
            return null;
        }

        poiCreated.setChargingStationObj(chargingStationCreated);

        if(poi.getPaymentMethodsObj() != null && !poi.getPaymentMethodsObj().isEmpty() && poi.getId()!= null){
            for(var currentPaymentMethod : poi.getPaymentMethodsObj()){
                currentPaymentMethod.setPoiId(poi.getId());
                paymentMethodFeignClient.createPaymentMethod(currentPaymentMethod);
            }
        }

        return poiCreated;
    }

    public POIDto getPOI(String poiId){
        if(poiId == null){
            logger.log(Level.WARNING, "poi id is null");
            return null;
        }
        var poiFound = poiRepository.get(poiId);
        if(poiFound == null){
            return null;
        }
        if(poiFound.getChargingStationId() == null){
            logger.log(Level.WARNING, "charging station id is null");
            return null;
        }
        var chargingStationFound = chargingStationRepository.get(poiFound.getChargingStationId());
        if(chargingStationFound == null){
            return null;
        }
        poiFound.setChargingStationObj(chargingStationFound);
        if(poiFound.getUserId() == null){
            logger.log(Level.WARNING, "user id is null");
            return null;
        }
        var userFound = userFeignClient.getUser(poiFound.getUserId());
        if(userFound == null){
            return null;
        }
        poiFound.setUserObj(userFound);

        var paymentMethods = paymentMethodFeignClient.getPaymentMethodsByPoiId(poiFound.getId());
        if(paymentMethods != null && !paymentMethods.isEmpty()){
            poiFound.setPaymentMethodsObj(paymentMethods);
        }

        return poiFound;
    }

    public List<POIDto> getPOIs(){
        var poisList = new ArrayList<POIDto>();
        var poisFound = poiRepository.getAll();
        if(poisFound == null){
            return null;
        }
        for(var currentPoi: poisFound){
            var fullPoi = getPOI(currentPoi.getId());
            if(fullPoi != null){
                poisList.add(fullPoi);
            }
        }
        return poisList;
    }

    public List<POIDto> getPOIsWithAvailability(String vehicleId) {
        var allPois = getPOIs();
        if(allPois == null){
            logger.log(Level.WARNING, "pois get error occurred");
            return null;
        }

        if(vehicleId == null){
            return allPois;
        }

        var vehicleFound = vehicleFeignClient.getVehicle(vehicleId);
        if(vehicleFound == null){
            return allPois;
        }

        for(var currentPoi : allPois){
            var plugs = plugFeignClient.getPlugsByChargingStation(currentPoi.getChargingStationId());
            if(plugs.stream().anyMatch(plug -> plug.getPlugTypeId().equals(vehicleFound.getPlugTypeId()))){
                currentPoi.setAvailableSelectedVehicle(true);
            }
            else{
                currentPoi.setAvailableSelectedVehicle(false);
            }
        }
        return allPois;
    }

    public List<POIDto> getPOIsByUser(String userId){
        var poisList = new ArrayList<POIDto>();
        var poisFound = poiRepository.getAllByUser(userId);
        if(poisFound == null){
            return null;
        }
        for(var currentPoi: poisFound){
            var fullPoi = getPOI(currentPoi.getId());
            if(fullPoi != null){
                poisList.add(fullPoi);
            }
        }
        return poisList;
    }

    public POIDto updatePOI(POIDto editedPOI){
        var currentPoi = getPOI(editedPOI.getId());
        editedPOI.setUserId(currentPoi.getUserId());
        editedPOI.setChargingStationId(currentPoi.getChargingStationObj().getId());
        poiRepository.update(editedPOI);
        if(editedPOI.getChargingStationObj() != null){
            //if user delete image
            if(currentPoi.getChargingStationObj().getImageId() != null &&
                    editedPOI.getChargingStationObj().getImageId() == null){
                chargingStationRepository.deleteChargingStationImage(currentPoi.getChargingStationObj().getImageId());
            }
            chargingStationRepository.update(editedPOI.getChargingStationObj());
        }
        //delete all previous payment methods
        if(currentPoi.getPaymentMethodsObj() != null && !currentPoi.getPaymentMethodsObj().isEmpty()){
            for(var currentPaymentMethod : currentPoi.getPaymentMethodsObj()){
                paymentMethodFeignClient.deletePaymentMethod(currentPaymentMethod.getId());
            }
        }
        //add new payment methods
        if(editedPOI.getPaymentMethodsObj() != null && !editedPOI.getPaymentMethodsObj().isEmpty() && editedPOI.getId() != null){
            for(var currentPaymentMethod : editedPOI.getPaymentMethodsObj()){
                currentPaymentMethod.setPoiId(editedPOI.getId());
                paymentMethodFeignClient.createPaymentMethod(currentPaymentMethod);
            }
        }
        return editedPOI;
    }

    public Boolean deletePOI(String poiId){
        var poi = getPOI(poiId);
        if(poi == null){
            logger.log(Level.WARNING, "poi does not exist");
            return false;
        }

        var poiDeleted = poiRepository.delete(poi.getId());
        if(!poiDeleted){
            logger.log(Level.WARNING, "poi did not delete");
            return false;
        }
        //remove payment methods
        if(poi.getPaymentMethodsObj() != null && !poi.getPaymentMethodsObj().isEmpty()){
            for(var paymentMethod : poi.getPaymentMethodsObj()){
                paymentMethodFeignClient.deletePaymentMethod(paymentMethod.getId());
            }
        }

        //remove charging station and its image
        chargingStationRepository.delete(poi.getChargingStationId());
        if(poi.getChargingStationObj().getImageId() != null){
            chargingStationRepository.deleteChargingStationImage(poi.getChargingStationObj().getImageId());
        }

        return true;
    }
    
    public POIDto getPOIByChargingStation(String chargingStationId) {
    	if(chargingStationId == null){
            logger.log(Level.WARNING, "Plug id is  null");
            return null;
        }
        var poiFound = poiRepository.getByChargingStation(chargingStationId);
        if(poiFound == null){
            logger.log(Level.WARNING, "Poi is null");
            return null;
        }
        return poiFound;
    }

    public POIDto getPOIByLocation(Float latitude, Float longitude, String vehicleId) {
        if(latitude == null || longitude == null || vehicleId == null){
            logger.log(Level.WARNING, "lat long or vehicleId is null");
            return null;
        }
        //fetch selected vehicle
        var vehicleFound = vehicleFeignClient.getVehicle(vehicleId);
        if(vehicleFound == null){
            logger.log(Level.WARNING, "vehicle does not exist");
            return null;
        }
        //fetch plugs with the same type of selected vehicle
        var plugsFound = plugFeignClient.getPlugsByPlugType(vehicleFound.getPlugTypeId());
        if(plugsFound == null){
            logger.log(Level.WARNING, "plug does not exist");
            return null;
        }

        //get all available plug's charging station ids (+ if there is available timeslot for at least two hours)
        var chargingStationIds = new ArrayList<String>();
        for(var currentPlug: plugsFound){
            if(currentPlug.getAvailability() == Availability.AVAILABLE){
                var allReservations = getAllByPlug(currentPlug.getId());
                var currentTime = Calendar.getInstance();
                var twoHoursLater = Calendar.getInstance();
                twoHoursLater.add(Calendar.HOUR, 2);
                if(allReservations != null &&
                        !checkIfTimeOverlap(allReservations, currentTime.getTimeInMillis(), twoHoursLater.getTimeInMillis())){
                    chargingStationIds.add(currentPlug.getChargingStationId());
                }
            }
        }
        //get all charging stations which has auto accept mode
        var chargingStationAutoAccept = new ArrayList<ChargingStationDto>();
        for(var currentChargingStationId : chargingStationIds){
            var currentChargingStation = chargingStationRepository.get(currentChargingStationId);
            if(currentChargingStation != null && currentChargingStation.getAutoAccept()){
                chargingStationAutoAccept.add(currentChargingStation);
            }
        }
        //get all Pois
        var poisFound = new ArrayList<POIDto>();
        for(var currentChargingStation : chargingStationAutoAccept){
            var currentPoi = poiRepository.getByChargingStation(currentChargingStation.getId());
            if(currentPoi != null){
                poisFound.add(currentPoi);
            }
        }
        var closestPoi = getClosestPoi(poisFound, latitude, longitude);
        if(closestPoi == null){
            return null;
        }
        var completePoi = getPOI(closestPoi.getId());
        return completePoi;
    }

    private List<ReservationDto> getAllByPlug(String plugId) {
        if(plugId == null){
            logger.log(Level.WARNING, "Plug id is  null");
            return null;
        }
        var reservationsFound = reservationFeignClient.getReservationsByPlug(plugId);
        if(reservationsFound == null){
            logger.log(Level.WARNING, "Reservations is  null");
            return null;
        }
        return reservationsFound;
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

                if (end1.before(start2) || end2.before(start1)) {
                    timeRangeOverlap = true;
                    break;
                }
            }
        }
        return timeRangeOverlap;
    }

    private POIDto getClosestPoi(ArrayList<POIDto> pois, Float latitude, Float longitude) {
        POIDto closestPoi = null;
        var currentShortestDistance = Float.MAX_VALUE;
        for(var currentPoi : pois){
            try{
                var origin = latitude.toString() + "," + longitude.toString();
                var destination = currentPoi.getLatitude().toString() + "," + currentPoi.getLongitude().toString();
                var currentDistance = distanceService.getDistanceInMeters(origin, destination);
                //var currentDistance = distance(latitude, longitude, currentPoi.getLatitude(), currentPoi.getLongitude());
                if(currentDistance < currentShortestDistance){
                    closestPoi = currentPoi;
                    currentShortestDistance = currentDistance;
                }
            }
            catch (Exception e){
                logger.log(Level.WARNING, "Error while getting closest distance of POI");
            }
        }
        return closestPoi;
    }


    private static double distance(double lat1, double lon1, double lat2,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }
}
