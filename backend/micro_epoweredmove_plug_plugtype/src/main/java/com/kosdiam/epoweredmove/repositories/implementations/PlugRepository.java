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
import com.kosdiam.epoweredmove.models.Plug;
import com.kosdiam.epoweredmove.models.dtos.PlugDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class PlugRepository implements IPlugRepository {
    Logger logger = Logger.getLogger(PlugRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;

    private String plugNode;


    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        plugNode = context.getBean("plugNode", String.class);
    }

    private PlugDto plug;
    private List<PlugDto> plugs;
    private Boolean plugDeleted;

    @Override
    public PlugDto create(PlugDto newPlug) {
        var done = new CountDownLatch(1);
        try {
            plug = null;
            var newKey = firebaseDatabase.push().getKey();
            newPlug.setId(newKey);
            newPlug.setTimestamp(Calendar.getInstance().getTimeInMillis());
            firebaseDatabase.child(plugNode).child(newKey).setValue(modelMapper.map(newPlug, Plug.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    plug = newPlug;
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

        return plug;
    }

    @Override
    public PlugDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(plugNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        plug = modelMapper.map(dataSnapshot.getValue(Plug.class), PlugDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plug = null;
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

        return plug;
    }

    @Override
    public List<PlugDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            plugs = new ArrayList<>();
            firebaseDatabase.child(plugNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            plugs.add(modelMapper.map(paymentMethodSnap.getValue(Plug.class), PlugDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plugs = null;
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

        return plugs;
    }

    @Override
    public List<PlugDto> getAllByChargingStation(String chargingStationId) {
        var done = new CountDownLatch(1);
        try {
            plugs = new ArrayList<>();
            firebaseDatabase.child(plugNode).orderByChild("chargingStationId").equalTo(chargingStationId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            plugs.add(modelMapper.map(paymentMethodSnap.getValue(Plug.class), PlugDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plugs = null;
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

        return plugs;
    }

    @Override
    public List<PlugDto> getAllByPlugType(String plugTypeId) {
        var done = new CountDownLatch(1);
        try {
            plugs = new ArrayList<>();
            firebaseDatabase.child(plugNode).orderByChild("plugTypeId").equalTo(plugTypeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            plugs.add(modelMapper.map(paymentMethodSnap.getValue(Plug.class), PlugDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plugs = null;
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

        return plugs;
    }

    @Override
    public PlugDto update(PlugDto editedPlug) {
        var done = new CountDownLatch(1);
        try {
            plug = null;
            var currentPaymentMethod = get(editedPlug.getId());
            if(currentPaymentMethod != null){
                editedPlug.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(plugNode).child(editedPlug.getId()).setValue(modelMapper.map(editedPlug, Plug.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        plug = editedPlug;
                    }
                    else{
                        plug = null;
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

        return plug;
    }

    @Override
    public Boolean delete(String id) {
        plugDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(plugNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(Plug.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                plugDeleted = true;
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

        return plugDeleted;
    }
    
}
