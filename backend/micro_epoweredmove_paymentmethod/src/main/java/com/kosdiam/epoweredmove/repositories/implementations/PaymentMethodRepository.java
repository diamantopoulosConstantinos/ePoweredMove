package com.kosdiam.epoweredmove.repositories.implementations;

import java.util.ArrayList;
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
import com.kosdiam.epoweredmove.models.PaymentMethod;
import com.kosdiam.epoweredmove.models.dtos.PaymentMethodDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPaymentMethodRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class PaymentMethodRepository implements IPaymentMethodRepository {
    Logger logger = Logger.getLogger(PaymentMethodRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private String paymentMethodNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        paymentMethodNode = context.getBean("paymentMethodNode", String.class);
    }

    private PaymentMethodDto paymentMethod;
    private List<PaymentMethodDto> paymentMethods;
    private Boolean paymentMethodDeleted;

    @Override
    public PaymentMethodDto create(PaymentMethodDto newPaymentMethod) {
        var done = new CountDownLatch(1);
        try {
            paymentMethod = null;
            var newKey = firebaseDatabase.push().getKey();
            newPaymentMethod.setId(newKey);
            firebaseDatabase.child(paymentMethodNode).child(newKey).setValue(modelMapper.map(newPaymentMethod, PaymentMethod.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    paymentMethod = newPaymentMethod;
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

        return paymentMethod;
    }

    @Override
    public PaymentMethodDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(paymentMethodNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        paymentMethod = modelMapper.map(dataSnapshot.getValue(PaymentMethod.class), PaymentMethodDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    paymentMethod = null;
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

        return paymentMethod;
    }

    @Override
    public List<PaymentMethodDto> getByPoiId(String id) {
        var done = new CountDownLatch(1);
        try {
            paymentMethods = new ArrayList<>();
            firebaseDatabase.child(paymentMethodNode).orderByChild("poiId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            paymentMethods.add(modelMapper.map(paymentMethodSnap.getValue(PaymentMethod.class), PaymentMethodDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    paymentMethod = null;
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

        return paymentMethods;
    }

    @Override
    public List<PaymentMethodDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            paymentMethods = new ArrayList<>();
            firebaseDatabase.child(paymentMethodNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            paymentMethods.add(modelMapper.map(paymentMethodSnap.getValue(PaymentMethod.class), PaymentMethodDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    paymentMethods = null;
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

        return paymentMethods;
    }

    @Override
    public PaymentMethodDto update(String id, PaymentMethodDto editedPaymentMethod) {
        var done = new CountDownLatch(1);
        try {
            paymentMethod = null;
            var currentPaymentMethod = get(id);
            if(currentPaymentMethod != null){
                editedPaymentMethod.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(paymentMethodNode).child(id).setValue(modelMapper.map(editedPaymentMethod, PaymentMethod.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        paymentMethod = editedPaymentMethod;
                    }
                    else{
                        paymentMethod = null;
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

        return paymentMethod;
    }

    @Override
    public Boolean delete(String id) {
        paymentMethodDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(paymentMethodNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(PaymentMethod.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                paymentMethodDeleted = true;
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

        return paymentMethodDeleted;
    }

}
