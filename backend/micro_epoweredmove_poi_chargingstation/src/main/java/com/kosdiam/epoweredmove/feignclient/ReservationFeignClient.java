package com.kosdiam.epoweredmove.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.ReservationDto;


@FeignClient(name = "reviewsreservations" , path="/epoweredmove/reservation" )
public interface ReservationFeignClient {
	
	@RequestMapping(path = "allByPlug", method = RequestMethod.GET)
    List<ReservationDto> getReservationsByPlug(@RequestParam String plugId) ;


}
