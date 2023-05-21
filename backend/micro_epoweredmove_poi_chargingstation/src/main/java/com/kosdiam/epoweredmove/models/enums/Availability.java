package com.kosdiam.epoweredmove.models.enums;

public enum Availability {
    UNAVAILABLE("Unavailable"),
    AVAILABLE("Available"),
    INUSE("In use"),
    UNKNOWN("Unknown");

    private String availability;
    private Availability(String availability){
        this.availability = availability;
    }

    @Override
    public String toString(){
        return availability;
    }
}
