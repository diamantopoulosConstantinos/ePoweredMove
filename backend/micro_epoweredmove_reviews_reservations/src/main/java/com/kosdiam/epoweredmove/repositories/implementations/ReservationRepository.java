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
import com.kosdiam.epoweredmove.models.Reservation;
import com.kosdiam.epoweredmove.models.dtos.ReservationDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IReservationRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class ReservationRepository implements IReservationRepository {
    Logger logger = Logger.getLogger(ReservationRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private String reservationNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        reservationNode = context.getBean("reservationNode", String.class);
    }

    private ReservationDto reservation;
    private List<ReservationDto> reservations;
    private Boolean reservationDeleted;

    @Override
    public ReservationDto create(ReservationDto newReservation) {
        var done = new CountDownLatch(1);
        try {
            reservation = null;
            var newKey = firebaseDatabase.push().getKey();
            newReservation.setId(newKey);
            newReservation.setTimestamp(Calendar.getInstance().getTimeInMillis());
            newReservation.setCancelled(false);
            firebaseDatabase.child(reservationNode).child(newKey).setValue(modelMapper.map(newReservation, Reservation.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    reservation = newReservation;
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

        return reservation;
    }

    @Override
    public ReservationDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(reservationNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        reservation = modelMapper.map(dataSnapshot.getValue(Reservation.class), ReservationDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reservation = null;
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

        return reservation;
    }

    @Override
    public List<ReservationDto> getAllByPlugId(String plugId){
        var done = new CountDownLatch(1);
        try {
            reservations = new ArrayList<>();
            firebaseDatabase.child(reservationNode).orderByChild("plugId").equalTo(plugId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            reservations.add(modelMapper.map(paymentMethodSnap.getValue(Reservation.class), ReservationDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reservations = null;
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

        return reservations;
    }

    @Override
    public List<ReservationDto> getAllByOwner(String userId){
        var done = new CountDownLatch(1);
        try {
            reservations = new ArrayList<>();
            firebaseDatabase.child(reservationNode).orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            reservations.add(modelMapper.map(paymentMethodSnap.getValue(Reservation.class), ReservationDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reservations = null;
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

        return reservations;
    }

    @Override
    public List<ReservationDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            reservations = new ArrayList<>();
            firebaseDatabase.child(reservationNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            reservations.add(modelMapper.map(paymentMethodSnap.getValue(Reservation.class), ReservationDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reservations = null;
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

        return reservations;
    }

    @Override
    public ReservationDto update(ReservationDto editedReservation) {
        var done = new CountDownLatch(1);
        try {
            reservation = null;
            var currentPaymentMethod = get(editedReservation.getId());
            if(currentPaymentMethod != null){
                editedReservation.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(reservationNode).child(editedReservation.getId()).setValue(modelMapper.map(editedReservation, Reservation.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        reservation = editedReservation;
                    }
                    else{
                        reservation = null;
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

        return reservation;
    }

    @Override
    public Boolean delete(String id) {
        reservationDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(reservationNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(Reservation.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                reservationDeleted = true;
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

        return reservationDeleted;
    }
}
