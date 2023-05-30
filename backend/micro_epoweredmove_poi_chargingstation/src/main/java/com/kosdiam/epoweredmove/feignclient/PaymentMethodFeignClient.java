package com.kosdiam.epoweredmove.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kosdiam.epoweredmove.models.dtos.PaymentMethodDto;

import feign.Capability;
import io.micrometer.core.instrument.MeterRegistry;

@FeignClient(name = "paymentmethod" , path="/epoweredmove/paymentMethod" )
public interface PaymentMethodFeignClient {

    @RequestMapping(method = RequestMethod.GET)
    PaymentMethodDto getPaymentMethod(@RequestParam String id);

    @RequestMapping(path = "all", method = RequestMethod.GET)
    List<PaymentMethodDto> getPaymentMethods() ;
    
    @RequestMapping(path = "paymentMethodsByPoiId", method = RequestMethod.GET)
    List<PaymentMethodDto> getPaymentMethodsByPoiId(@RequestParam String id) ;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentMethodDto createPaymentMethod(@RequestBody PaymentMethodDto paymentMethodRequest);

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    PaymentMethodDto updatePaymentMethod(@RequestParam String id, @RequestBody PaymentMethodDto paymentMethodRequest);

    @RequestMapping(method = RequestMethod.DELETE)
    Boolean deletePaymentMethod(@RequestParam String id);

	
        
	
}
