package com.kosdiam.epoweredmove.repositories.implementations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kosdiam.epoweredmove.models.POI;
import com.kosdiam.epoweredmove.models.dtos.POIDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPOIRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class POIRepository implements IPOIRepository {
    Logger logger = Logger.getLogger(POIRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private String poiNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        poiNode = context.getBean("poiNode", String.class);
    }

    private POIDto poi;
    private List<POIDto> pois;
    private Boolean poiDeleted;

    @Override
    public POIDto create(POIDto newPOI) {
        var done = new CountDownLatch(1);
        try {
            poi = null;
            var newKey = firebaseDatabase.push().getKey();
            newPOI.setId(newKey);
            newPOI.setTimestamp(Calendar.getInstance().getTimeInMillis());
            firebaseDatabase.child(poiNode).child(newKey).setValue(modelMapper.map(newPOI, POI.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    poi = newPOI;
                }
                else{
                    logger.log(Level.SEVERE, databaseError.getMessage());
                }
                done.countDown();
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return poi;
    }

    @Override
    public POIDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(poiNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        poi = modelMapper.map(dataSnapshot.getValue(POI.class), POIDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    poi = null;
                    done.countDown();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return poi;
    }

    @Override
    public POIDto getByChargingStation(String chargingStationId) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(poiNode).orderByChild("chargingStationId").equalTo(chargingStationId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var poiSnap : dataSnapshot.getChildren()){
                            poi = modelMapper.map(poiSnap.getValue(POI.class), POIDto.class);
                            break;
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    poi = null;
                    done.countDown();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return poi;
    }

    @Override
    public List<POIDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            pois = new ArrayList<>();
            firebaseDatabase.child(poiNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            pois.add(modelMapper.map(paymentMethodSnap.getValue(POI.class), POIDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    pois = null;
                    done.countDown();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return pois;
    }

    @Override
    public List<POIDto> getAllByUser(String userId) {
        var done = new CountDownLatch(1);
        try {
            pois = new ArrayList<>();
            firebaseDatabase.child(poiNode).orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            pois.add(modelMapper.map(paymentMethodSnap.getValue(POI.class), POIDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    pois = null;
                    done.countDown();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return pois;
    }

    @Override
    public POIDto update(POIDto editedPOI) {
        var done = new CountDownLatch(1);
        try {
            poi = null;
            var currentPaymentMethod = get(editedPOI.getId());
            if(currentPaymentMethod != null){
                editedPOI.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(poiNode).child(editedPOI.getId()).setValue(modelMapper.map(editedPOI, POI.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        poi = editedPOI;
                    }
                    else{
                        poi = null;
                        logger.log(Level.SEVERE, databaseError.getMessage());
                    }
                    done.countDown();
                });
            }
            else{
                done.countDown();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return poi;
    }

    @Override
    public Boolean delete(String id) {
        poiDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(poiNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(POI.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                poiDeleted = true;
                            }
                            else {
                                logger.log(Level.SEVERE, databaseError.getMessage());
                            }
                            done.countDown();
                        });
                    }
                    else{
                        done.countDown();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    done.countDown();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            done.countDown();
        }
        // wait for firebase API response
        try{
            done.await();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return poiDeleted;
    }
}
