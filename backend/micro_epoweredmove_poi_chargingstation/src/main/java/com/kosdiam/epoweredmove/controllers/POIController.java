package com.kosdiam.epoweredmove.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosdiam.epoweredmove.models.dtos.POIDto;
import com.kosdiam.epoweredmove.services.POIService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/poi")
public class POIController {
    private final POIService poiService;

    public POIController(POIService poiService) {
        this.poiService = poiService;
    }
    
    @RequestMapping(path = "hello", method = RequestMethod.GET)
    @RateLimiter(name = "hello", fallbackMethod = "helloFallback")
    public String hello() {
    	Optional<String> podName = Optional.ofNullable(System.getenv("HOSTNAME"));
		return "Hello, Welcome to K8s cluster = " + podName.get();
    }
    
    private String helloFallback(Throwable t) {
		return "Hello (fallback)";
	}
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<POIDto> getPOI(@RequestParam String id) {
        var poi = poiService.getPOI(id);
        if(poi == null) {
            throw new RecordNotFoundException("POI not exist");
        }
        return new ResponseEntity<>(poi, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<POIDto>> getPOIs() {
        var pois = poiService.getPOIs();
        if(pois == null) {
            throw new RecordNotFoundException("POI error occurred");
        }
        return new ResponseEntity<>(pois, HttpStatus.OK);
    }

    @RequestMapping(path = "allWithPlugAvailability", method = RequestMethod.GET)
    public ResponseEntity<List<POIDto>> getPOIsWithAvailability(@RequestParam(required = false) String vehicleId) {
        var pois = poiService.getPOIsWithAvailability(vehicleId);
        if(pois == null) {
            throw new RecordNotFoundException("POI error occurred");
        }
        return new ResponseEntity<>(pois, HttpStatus.OK);
    }

    @RequestMapping(path = "allByUser", method = RequestMethod.GET)
    public ResponseEntity<List<POIDto>> getPOIsByUser(@RequestParam String userId) {
        var pois = poiService.getPOIsByUser(userId);
        if(pois == null) {
            throw new RecordNotFoundException("POI error occurred");
        }
        return new ResponseEntity<>(pois, HttpStatus.OK);
    }
    
    @RequestMapping(path = "getPoiByChargingStation", method = RequestMethod.GET)
    public ResponseEntity<POIDto> getPoiByChargingStation(@RequestParam String chargingStationId) {
        var poi = poiService.getPOIByChargingStation(chargingStationId);
        if(poi == null) {
            throw new RecordNotFoundException("POI error occurred");
        }
        return new ResponseEntity<>(poi, HttpStatus.OK);
    }

    @RequestMapping(path = "searchPoiByLocation", method = RequestMethod.GET)
    public ResponseEntity<POIDto> getPOIByLocation(@RequestParam Float latitude, @RequestParam Float longitude, @RequestParam String vehicleId) {
        var poi = poiService.getPOIByLocation(latitude, longitude, vehicleId);
        if(poi == null) {
            throw new RecordNotFoundException("POI error occurred");
        }
        return new ResponseEntity<>(poi, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<POIDto> createPOI(@RequestBody POIDto poiRequest) {
        var poi = poiService.createPOI(poiRequest);
        if(poi == null) {
            throw new RecordNotFoundException("POI not created");
        }
        return new ResponseEntity<>(poi, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<POIDto> updatePOI(@RequestBody POIDto poiRequest) {
        var poi = poiService.updatePOI(poiRequest);
        if(poi == null) {
            throw new RecordNotFoundException("POI not exist");
        }
        return new ResponseEntity<>(poi, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePOI(@RequestParam String id) {
        var poiDeleted = poiService.deletePOI(id);
        if(!poiDeleted) {
            throw new RecordNotFoundException("POI not exist");
        }
        return new ResponseEntity<>(poiDeleted, HttpStatus.OK);
    }
}
