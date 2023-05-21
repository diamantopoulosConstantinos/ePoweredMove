package com.kosdiam.epoweredmove.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.PlugDto;

@FeignClient(name = "plugplugtype" , path="/epoweredmove/plug" )
public interface PlugFeignClient {
	
	@RequestMapping(method = RequestMethod.GET)
    PlugDto getPlug(@RequestParam String id) ;
	
	@RequestMapping(path = "allByChargingStation", method = RequestMethod.GET)
    List<PlugDto> getPlugsByChargingStation(@RequestParam String chargingStationId);
	
	@RequestMapping(path = "allByPlugType", method = RequestMethod.GET)
    List<PlugDto> getPlugsByPlugType(@RequestParam String plugTypeId);
        

}
