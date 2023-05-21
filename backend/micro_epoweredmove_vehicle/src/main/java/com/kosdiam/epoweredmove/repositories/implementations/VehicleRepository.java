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
import com.kosdiam.epoweredmove.models.Vehicle;
import com.kosdiam.epoweredmove.models.dtos.VehicleDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IVehicleRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class VehicleRepository implements IVehicleRepository {
    Logger logger = Logger.getLogger(VehicleRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private String vehicleNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        vehicleNode = context.getBean("vehicleNode", String.class);
    }

    private VehicleDto vehicle;
    private List<VehicleDto> vehicles;
    private Boolean vehicleDeleted;

    @Override
    public VehicleDto create(VehicleDto newVehicle) {
        var done = new CountDownLatch(1);
        try {
            vehicle = null;
            var newKey = firebaseDatabase.push().getKey();
            newVehicle.setId(newKey);
            newVehicle.setTimestamp(Calendar.getInstance().getTimeInMillis());
            firebaseDatabase.child(vehicleNode).child(newKey).setValue(modelMapper.map(newVehicle, Vehicle.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    vehicle = newVehicle;
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

        return vehicle;
    }

    @Override
    public VehicleDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(vehicleNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        vehicle = modelMapper.map(dataSnapshot.getValue(Vehicle.class), VehicleDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    vehicle = null;
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

        return vehicle;
    }

    @Override
    public List<VehicleDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            vehicles = new ArrayList<>();
            firebaseDatabase.child(vehicleNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            vehicles.add(modelMapper.map(paymentMethodSnap.getValue(Vehicle.class), VehicleDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    vehicles = null;
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

        return vehicles;
    }

    @Override
    public List<VehicleDto> getAllByUserId(String userId) {
        var done = new CountDownLatch(1);
        try {
            vehicles = new ArrayList<>();
            firebaseDatabase.child(vehicleNode).orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var vehicleSnap : dataSnapshot.getChildren()){
                            vehicles.add(modelMapper.map(vehicleSnap.getValue(Vehicle.class), VehicleDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    vehicles = null;
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

        return vehicles;
    }

    @Override
    public VehicleDto update(VehicleDto editedVehicle) {
        var done = new CountDownLatch(1);
        try {
            vehicle = null;
            var currentPaymentMethod = get(editedVehicle.getId());
            if(currentPaymentMethod != null){
                editedVehicle.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(vehicleNode).child(editedVehicle.getId()).setValue(modelMapper.map(editedVehicle, Vehicle.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        vehicle = editedVehicle;
                    }
                    else{
                        vehicle = null;
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

        return vehicle;
    }

    @Override
    public Boolean delete(String id) {
        vehicleDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(vehicleNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(Vehicle.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                vehicleDeleted = true;
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

        return vehicleDeleted;
    }
}
