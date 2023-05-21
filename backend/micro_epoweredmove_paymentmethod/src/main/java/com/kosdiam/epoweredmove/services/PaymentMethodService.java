package com.kosdiam.epoweredmove.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.models.dtos.PaymentMethodDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPaymentMethodRepository;

@Service
public class PaymentMethodService {
    @Autowired
    private final IPaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(IPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public PaymentMethodDto createPaymentMethod(PaymentMethodDto paymentMethod){
        return paymentMethodRepository.create(paymentMethod);
    }

    public PaymentMethodDto getPaymentMethod(String paymentMethodId){
        return paymentMethodRepository.get(paymentMethodId);
    }

    public List<PaymentMethodDto> getPaymentMethods(){
        return paymentMethodRepository.getAll();
    }

    public PaymentMethodDto updatePaymentMethod(String paymentMethodId, PaymentMethodDto editedPaymentMethod){
        return paymentMethodRepository.update(paymentMethodId, editedPaymentMethod);
    }

    public Boolean deletePaymentMethod(String paymentMethodId){
        return paymentMethodRepository.delete(paymentMethodId);
    }
    
    public List<PaymentMethodDto> getByPoiId(String id){
    	return paymentMethodRepository.getByPoiId(id);
    }
}
