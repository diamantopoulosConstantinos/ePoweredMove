package com.kosdiam.epoweredmove.models.enums;

public enum CurrentType {
    AC("AC"),
    DC("DC");

    private String currentType;
    private CurrentType(String currentType){
        this.currentType = currentType;
    }

    @Override
    public String toString(){
        return currentType;
    }
}
