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

import com.kosdiam.epoweredmove.models.dtos.PaymentMethodDto;
import com.kosdiam.epoweredmove.services.PaymentMethodService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("epoweredmove/paymentMethod")
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
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
    public ResponseEntity<PaymentMethodDto> getPaymentMethod(@RequestParam String id) {
        var paymentMethod = paymentMethodService.getPaymentMethod(id);
        if(paymentMethod == null) {
            throw new RecordNotFoundException("PaymentMethod not exist");
        }
        return new ResponseEntity<>(paymentMethod, HttpStatus.OK);
    }
    
    @RequestMapping(path = "paymentMethodsByPoiId", method = RequestMethod.GET)
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethodsByPoiId(@RequestParam String id) {
        var paymentMethods = paymentMethodService.getByPoiId(id);
        if(paymentMethods == null || paymentMethods.isEmpty()) {
            throw new RecordNotFoundException("PaymentMethod error occurred");
        }
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods() {
        var paymentMethods = paymentMethodService.getPaymentMethods();
        if(paymentMethods == null) {
            throw new RecordNotFoundException("PaymentMethod error occurred");
        }
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentMethodDto> createPaymentMethod(@RequestBody PaymentMethodDto paymentMethodRequest) {
        var paymentMethod = paymentMethodService.createPaymentMethod(paymentMethodRequest);
        if(paymentMethod == null) {
            throw new RecordNotFoundException("PaymentMethod not created");
        }
        return new ResponseEntity<>(paymentMethod, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentMethodDto> updatePaymentMethod(@RequestParam String id, @RequestBody PaymentMethodDto paymentMethodRequest) {
        var paymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethodRequest);
        if(paymentMethod == null) {
            throw new RecordNotFoundException("ChargingStation not exist");
        }
        return new ResponseEntity<>(paymentMethod, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePaymentMethod(@RequestParam String id) {
        var paymentMethodDeleted = paymentMethodService.deletePaymentMethod(id);
        if(!paymentMethodDeleted) {
            throw new RecordNotFoundException("ChargingStation not exist");
        }
        return new ResponseEntity<>(paymentMethodDeleted, HttpStatus.OK);
    }
}
