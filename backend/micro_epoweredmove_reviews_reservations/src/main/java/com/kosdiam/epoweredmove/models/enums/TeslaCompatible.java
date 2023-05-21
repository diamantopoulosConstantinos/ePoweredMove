package com.kosdiam.epoweredmove.models.enums;

public enum TeslaCompatible {
    YES("Yes"),
    NO("No");

    private String teslaCompatible;
    private TeslaCompatible(String teslaCompatible){
        this.teslaCompatible = teslaCompatible;
    }

    @Override
    public String toString(){
        return teslaCompatible;
    }
}
