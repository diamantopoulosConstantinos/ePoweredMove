package com.kosdiam.epoweredmove.feignclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.UserDto;


@FeignClient(name = "user" , path="/epoweredmove/user" )
public interface UserFeignClient {
	
	
	@RequestMapping(method = RequestMethod.POST, value = "verifyToken", consumes = "application/json")
	Boolean verifyToken(@RequestHeader String authorization);
	
	@RequestMapping(method = RequestMethod.GET)
    UserDto getUser(@RequestParam String id);

}