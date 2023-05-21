package com.kosdiam.epoweredmove.models.enums;

public enum ReviewStatus {
    ACTIVE("Active"),
    CANCELLED("Cancelled");

    private String reviewStatus;
    private ReviewStatus(String currentType){
        this.reviewStatus = reviewStatus;
    }

    @Override
    public String toString(){
        return reviewStatus;
    }
}
