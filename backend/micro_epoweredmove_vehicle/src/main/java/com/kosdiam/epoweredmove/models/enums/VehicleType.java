package com.kosdiam.epoweredmove.models.enums;

public enum VehicleType {
    CAR("Car"),
    MOTORBIKE("Motorbike"),
    SCOOTER("Scooter"),
    BICYCLE("Bicycle"),
    OTHER("Other");

    private String vehicleType;
    private VehicleType(String vehicleType){
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString(){
        return vehicleType;
    }
}
