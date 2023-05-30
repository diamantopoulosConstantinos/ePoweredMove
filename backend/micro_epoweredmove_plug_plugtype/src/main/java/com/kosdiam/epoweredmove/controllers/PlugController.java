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

import com.kosdiam.epoweredmove.models.dtos.PlugDto;
import com.kosdiam.epoweredmove.services.PlugService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/plug")
public class PlugController {
    private final PlugService plugService;

    public PlugController(PlugService plugService) {
        this.plugService = plugService;
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
    public ResponseEntity<PlugDto> getPlug(@RequestParam String id) {
        var plug = plugService.getPlug(id);
        if(plug == null) {
            throw new RecordNotFoundException("Plug not exist");
        }
        return new ResponseEntity<>(plug, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<PlugDto>> getPlugs() {
        var plugs = plugService.getPlugs();
        if(plugs == null) {
            throw new RecordNotFoundException("Plug error occurred");
        }
        return new ResponseEntity<>(plugs, HttpStatus.OK);
    }

    @RequestMapping(path = "allByChargingStation", method = RequestMethod.GET)
    public ResponseEntity<List<PlugDto>> getPlugsByChargingStation(@RequestParam String chargingStationId) {
        var plugs = plugService.getPlugsByChargingStation(chargingStationId);
        if(plugs == null) {
            throw new RecordNotFoundException("Plug error occurred");
        }
        return new ResponseEntity<>(plugs, HttpStatus.OK);
    }
    
    @RequestMapping(path = "allByPlugType", method = RequestMethod.GET)
    public ResponseEntity<List<PlugDto>> getPlugsByPlugType(@RequestParam String plugTypeId) {
        var plugs = plugService.getPlugsByPlugType(plugTypeId);
        if(plugs == null) {
            throw new RecordNotFoundException("Plug error occurred");
        }
        return new ResponseEntity<>(plugs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlugDto> createPlug(@RequestBody PlugDto plugRequest) {
        var plug = plugService.createPlug(plugRequest);
        if(plug == null) {
            throw new RecordNotFoundException("Plug not created");
        }
        return new ResponseEntity<>(plug, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlugDto> updatePlug(@RequestBody PlugDto plugRequest) {
        var plug = plugService.updatePlug(plugRequest);
        if(plug == null) {
            throw new RecordNotFoundException("Plug not exist");
        }
        return new ResponseEntity<>(plug, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePlug(@RequestParam String id) {
        var plugDeleted = plugService.deletePlug(id);
        if(!plugDeleted) {
            throw new RecordNotFoundException("Plug not exist");
        }
        return new ResponseEntity<>(plugDeleted, HttpStatus.OK);
    }

}
