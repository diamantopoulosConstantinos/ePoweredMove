package com.kosdiam.epoweredmove.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;
import com.kosdiam.epoweredmove.models.dtos.POIDto;

@FeignClient(name = "poichargingstation" , path="/epoweredmove" )
public interface PoiChargingStationFeignClient {

	@RequestMapping(path = "poi/getPoiByChargingStation", method = RequestMethod.GET)
    POIDto getPoiByChargingStation(@RequestParam String chargingStationId);
	
	@RequestMapping(path = "poi/allByUser", method = RequestMethod.GET)
    List<POIDto> getPOIsByUser(@RequestParam String userId) ;
	
	@RequestMapping(method = RequestMethod.GET, path="chargingStation")
    ChargingStationDto getChargingStation(@RequestParam String id);
}
