package com.kosdiam.epoweredmove.feignclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;

@FeignClient(name = "plugplugtype" , path="/epoweredmove/plugType" )
public interface PlugTypeFeignClient {
	
	@RequestMapping(method = RequestMethod.GET)
    PlugTypeDto getPlugType(@RequestParam String id);

	
}
