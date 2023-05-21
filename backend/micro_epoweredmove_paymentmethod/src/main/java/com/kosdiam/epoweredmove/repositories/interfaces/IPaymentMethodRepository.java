package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.PaymentMethodDto;

@Repository
public interface IPaymentMethodRepository {
    PaymentMethodDto create(PaymentMethodDto paymentMethod);
    PaymentMethodDto get(String id);
    List<PaymentMethodDto> getAll();
    PaymentMethodDto update(String id, PaymentMethodDto paymentMethod);
    Boolean delete(String id);
    List<PaymentMethodDto> getByPoiId(String id);
}
