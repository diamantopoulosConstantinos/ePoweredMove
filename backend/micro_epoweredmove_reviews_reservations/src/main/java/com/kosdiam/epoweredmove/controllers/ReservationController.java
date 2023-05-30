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

import com.kosdiam.epoweredmove.models.dtos.ReservationDto;
import com.kosdiam.epoweredmove.models.dtos.RouteInfoDto;
import com.kosdiam.epoweredmove.services.ReservationService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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
    public ResponseEntity<ReservationDto> getReservation(@RequestParam String id) {
        var reservation = reservationService.getReservation(id);
        if(reservation == null) {
            throw new RecordNotFoundException("Reservation not exist");
        }
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<ReservationDto>> getReservations() {
        var reservations = reservationService.getReservations();
        if(reservations == null) {
            throw new RecordNotFoundException("Reservation error occurred");
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @RequestMapping(path = "allByChargingStationOwner", method = RequestMethod.GET)
    public ResponseEntity<List<ReservationDto>> getReservationsByChargingStationOwner(@RequestParam String userId) {
        var reservations = reservationService.getReservationsByChargingStationOwner(userId);
        if(reservations == null) {
            throw new RecordNotFoundException("Reservations error occurred");
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @RequestMapping(path = "allByPlug", method = RequestMethod.GET)
    public ResponseEntity<List<ReservationDto>> getReservationsByPlug(@RequestParam String plugId) {
        var reservations = reservationService.getAllByPlug(plugId);
        if(reservations == null) {
            throw new RecordNotFoundException("Reservations error occurred");
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @RequestMapping(path = "allByOwner", method = RequestMethod.GET)
    public ResponseEntity<List<ReservationDto>> getReservationsByOwner(@RequestParam String userId) {
        var reservations = reservationService.getReservationsByOwner(userId);
        if(reservations == null) {
            throw new RecordNotFoundException("Reservations error occurred");
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @RequestMapping(path = "routeInfoByReservation", method = RequestMethod.GET)
    public ResponseEntity<RouteInfoDto> getRouteInfoByReservation(@RequestParam String reservationId,
                                                                  @RequestParam Integer batteryPercentageRemaining,
                                                                  @RequestParam Float currentLatitude,
                                                                  @RequestParam Float currentLongitude) {
        var reservationInfo = reservationService.getRouteInfoByReservation(reservationId, batteryPercentageRemaining, currentLatitude, currentLongitude);
        if(reservationInfo == null) {
            throw new RecordNotFoundException("Reservations error occurred");
        }
        return new ResponseEntity<>(reservationInfo, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationRequest) {
        var reservation = reservationService.createReservation(reservationRequest);
        if(reservation == null) {
            throw new RecordNotFoundException("Reservation not created");
        }
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> updateReservation(@RequestBody ReservationDto reservationRequest) {
        var reservation = reservationService.updateReservation(reservationRequest);
        if(reservation == null) {
            throw new RecordNotFoundException("Reservation not exist");
        }
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteReservation(@RequestParam String id) {
        var reservationDeleted = reservationService.deleteReservation(id);
        if(!reservationDeleted) {
            throw new RecordNotFoundException("Reservation not exist");
        }
        return new ResponseEntity<>(reservationDeleted, HttpStatus.OK);
    }
}
