package com.kosdiam.epoweredmove.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FirebaseNodeConfig {
    @Bean
    public String chargingStationNode(){
        return chargingStationNode;
    }
    @Bean
    public String paymentMethodNode(){
        return paymentMethodNode;
    }
    @Bean
    public String plugNode(){
        return plugNode;
    }
    @Bean
    public String plugTypeNode(){
        return plugTypeNode;
    }
    @Bean
    public String poiNode(){
        return poiNode;
    }
    @Bean
    public String reservationNode(){
        return reservationNode;
    }
    @Bean
    public String reviewNode(){
        return reviewNode;
    }
    @Bean
    public String userNode(){
        return userNode;
    }
    @Bean
    public String roleNode(){
        return roleNode;
    }
    @Bean
    public String vehicleNode(){
        return vehicleNode;
    }
    @Bean
    public String storageBucket(){
        return storageBucket;
    }
    @Bean
    public String plugTypeStorageNode(){
        return plugTypeStorageNode;
    }
    @Bean
    public String chargingStationStorageNode(){
        return chargingStationStorageNode;
    }

    @Value("${com.kosdiam.firebase.database.node.charging-stations}")
    private String chargingStationNode;
    @Value("${com.kosdiam.firebase.database.node.payment-methods}")
    private String paymentMethodNode;
    @Value("${com.kosdiam.firebase.database.node.plugs}")
    private String plugNode;
    @Value("${com.kosdiam.firebase.database.node.plug-types}")
    private String plugTypeNode;
    @Value("${com.kosdiam.firebase.database.node.pois}")
    private String poiNode;
    @Value("${com.kosdiam.firebase.database.node.reservations}")
    private String reservationNode;
    @Value("${com.kosdiam.firebase.database.node.reviews}")
    private String reviewNode;
    @Value("${com.kosdiam.firebase.database.node.users}")
    private String userNode;

    @Value("${com.kosdiam.firebase.database.node.roles}")
    private String roleNode;
    @Value("${com.kosdiam.firebase.database.node.vehicles}")
    private String vehicleNode;
    @Value("${com.kosdiam.firebase.storage.bucket}")
    private String storageBucket;
    @Value("${com.kosdiam.firebase.storage.node.charging-stations}")
    private String chargingStationStorageNode;
    @Value("${com.kosdiam.firebase.storage.node.plug-types}")
    private String plugTypeStorageNode;
}
