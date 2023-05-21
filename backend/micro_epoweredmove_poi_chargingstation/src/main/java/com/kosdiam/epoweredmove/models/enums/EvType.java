package com.kosdiam.epoweredmove.models.enums;

public enum EvType {
    MHEV("Mild hybrid electric vehicle"),
    HEV("Hybrid electric vehicle"),
    PHEV("Plug-in hybrid electric vehicle"),
    BEV("Battery electric vehicle");

    private String evType;
    private EvType(String evType){
        this.evType = evType;
    }

    @Override
    public String toString(){
        return evType;
    }
}
