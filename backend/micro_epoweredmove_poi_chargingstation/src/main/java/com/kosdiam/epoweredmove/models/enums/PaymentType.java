package com.kosdiam.epoweredmove.models.enums;

public enum PaymentType {
    APP("App"),
    PAYPAL("Paypal"),
    EBANKING("E-banking"),
    LOCALLY("Locally");

    private String paymentType;
    private PaymentType(String paymentType){
        this.paymentType = paymentType;
    }

    @Override
    public String toString(){
        return paymentType;
    }
}
