package com.kosdiam.epoweredmove.utils.maps;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

@Service
public class DistanceService {

    private final GeoApiContext context;
    @Autowired
    private ApplicationContext appContext;

    //private String apiKey;

    @PostConstruct
    public void init(){
        //apiKey = appContext.getBean("googleMapsKey", String.class);
    }

    public DistanceService(@Value("${com.kosdiam.google.maps.key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public long getDistanceInMeters(String origin, String destination) throws Exception {
        DirectionsApiRequest request = DirectionsApi.getDirections(this.context, origin, destination);
        DirectionsResult result = request.await();

        if (result.routes.length == 0) {
            throw new Exception("No routes found.");
        }
        //get shortest route
        DirectionsRoute route = result.routes[0];

        //get route's distance in meters
        return route.legs[0].distance.inMeters;
    }
}
