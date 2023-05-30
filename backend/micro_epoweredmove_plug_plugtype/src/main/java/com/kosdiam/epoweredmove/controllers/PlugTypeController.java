package com.kosdiam.epoweredmove.controllers;

import java.util.List;
import java.util.Optional;

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

import com.kosdiam.epoweredmove.models.dtos.ImageResponseDto;
import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;
import com.kosdiam.epoweredmove.services.PlugTypeService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/plugType")
public class PlugTypeController {
    private final PlugTypeService plugTypeService;

    public PlugTypeController(PlugTypeService plugTypeService) {
        this.plugTypeService = plugTypeService;
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
    public ResponseEntity<PlugTypeDto> getPlugType(@RequestParam String id) {
        var plugType = plugTypeService.getPlugType(id);
        if(plugType == null) {
            throw new RecordNotFoundException("Plug not exist");
        }
        return new ResponseEntity<>(plugType, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<PlugTypeDto>> getPlugTypes() {
        var plugTypes = plugTypeService.getPlugTypes();
        if(plugTypes == null) {
            throw new RecordNotFoundException("PlugType error occurred");
        }
        return new ResponseEntity<>(plugTypes, HttpStatus.OK);
    }

    @RequestMapping(path = "allByChargingStation", method = RequestMethod.GET)
    public ResponseEntity<List<PlugTypeDto>> getPlugTypesByChargingStation(@RequestParam String chargingStationId) {
        var plugTypes = plugTypeService.getPlugTypesByChargingStation(chargingStationId);
        if(plugTypes == null) {
            throw new RecordNotFoundException("PlugType error occurred");
        }
        return new ResponseEntity<>(plugTypes, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlugTypeDto> createPlugType(@RequestBody PlugTypeDto plugTypeRequest) {
        var plugType = plugTypeService.createPlugType(plugTypeRequest);
        if(plugType == null) {
            throw new RecordNotFoundException("PlugType not created");
        }
        return new ResponseEntity<>(plugType, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlugTypeDto> updatePlugType(@RequestBody PlugTypeDto plugTypeRequest) {
        var plugType = plugTypeService.updatePlugType(plugTypeRequest);
        if(plugType == null) {
            throw new RecordNotFoundException("PlugType not exist");
        }
        return new ResponseEntity<>(plugType, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePlugType(@RequestParam String id) {
        var plugTypeDeleted = plugTypeService.deletePlugType(id);
        if(!plugTypeDeleted) {
            throw new RecordNotFoundException("PlugType not exist");
        }
        return new ResponseEntity<>(plugTypeDeleted, HttpStatus.OK);
    }

    @RequestMapping(path = "addImage", method = RequestMethod.POST)
    public ResponseEntity<ImageResponseDto> addPlugTypeImage(@RequestParam() String id, @ModelAttribute() MultipartFile file) {
        var response =plugTypeService.addPlugTypeImage(id, file);
        if(response == null) {
            throw new RecordNotFoundException("Plug image not created");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "deleteImage", method = RequestMethod.DELETE)
    public ResponseEntity<ImageResponseDto> deletePlugTypeImage(@RequestParam() String id) {
        var response = plugTypeService.deletePlugTypeImage(id);
        if(response == null) {
            throw new RecordNotFoundException("Plug image not deleted");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
