package com.kosdiam.epoweredmove.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.PlugDto;
import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;

@FeignClient(name = "plugplugtype" , path="/epoweredmove" )
public interface PlugPlugTypeFeignClient {
	
	@RequestMapping(path="plug", method = RequestMethod.GET)
    PlugDto getPlug(@RequestParam String id) ;
	
	@RequestMapping(path = "plug/allByChargingStation", method = RequestMethod.GET)
    List<PlugDto> getPlugsByChargingStation(@RequestParam String chargingStationId);
	
	@RequestMapping(path = "plug/allByPlugType", method = RequestMethod.GET)
    List<PlugDto> getPlugsByPlugType(@RequestParam String plugTypeId); 
	
	@RequestMapping(path = "plugType", method = RequestMethod.GET)
    PlugTypeDto getPlugType(@RequestParam String id);

}
