package com.kosdiam.epoweredmove.models.enums;

public enum UserRole {
    ADMIN("Admin"),
    POI_OWNER("Point of Interest Owner"),
    USER("User");

    private String userRole;
    private UserRole(String userRole){
        this.userRole = userRole;
    }

    @Override
    public String toString(){
        return userRole;
    }
}
