package com.kosdiam.epoweredmove.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.VehicleDto;

@FeignClient(name = "vehicle" , path="/epoweredmove/vehicle" )
public interface VehicleFeignClient {
	
	@RequestMapping(method = RequestMethod.GET)
    VehicleDto getVehicle(@RequestParam String id );
       
}
