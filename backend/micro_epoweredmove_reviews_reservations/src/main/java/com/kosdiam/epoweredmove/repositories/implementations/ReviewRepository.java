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
import com.kosdiam.epoweredmove.models.Review;
import com.kosdiam.epoweredmove.models.dtos.ReviewDto;
import com.kosdiam.epoweredmove.models.enums.ReviewStatus;
import com.kosdiam.epoweredmove.repositories.interfaces.IReviewRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class ReviewRepository implements IReviewRepository {
    Logger logger = Logger.getLogger(ReviewRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private String reviewNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        reviewNode = context.getBean("reviewNode", String.class);
    }

    private ReviewDto review;
    private List<ReviewDto> reviews;
    private Boolean reviewDeleted;

    @Override
    public ReviewDto create(ReviewDto newReview) {
        var done = new CountDownLatch(1);
        try {
            review = null;
            var newKey = firebaseDatabase.push().getKey();
            newReview.setId(newKey);
            newReview.setTimestamp(Calendar.getInstance().getTimeInMillis());
            newReview.setStatus(ReviewStatus.ACTIVE);
            firebaseDatabase.child(reviewNode).child(newKey).setValue(modelMapper.map(newReview, Review.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    review = newReview;
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

        return review;
    }

    @Override
    public ReviewDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(reviewNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        review = modelMapper.map(dataSnapshot.getValue(Review.class), ReviewDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    review = null;
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

        return review;
    }

    @Override
    public List<ReviewDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            reviews = new ArrayList<>();
            firebaseDatabase.child(reviewNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            reviews.add(modelMapper.map(paymentMethodSnap.getValue(Review.class), ReviewDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reviews = null;
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

        return reviews;
    }

    @Override
    public List<ReviewDto> getAllByChargingStation(String chargingStationId) {
        var done = new CountDownLatch(1);
        try {
            reviews = new ArrayList<>();
            firebaseDatabase.child(reviewNode).orderByChild("chargingStationId").equalTo(chargingStationId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            reviews.add(modelMapper.map(paymentMethodSnap.getValue(Review.class), ReviewDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    reviews = null;
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

        return reviews;
    }

    @Override
    public ReviewDto update(String id, ReviewDto editedReview) {
        var done = new CountDownLatch(1);
        try {
            review = null;
            var currentPaymentMethod = get(id);
            if(currentPaymentMethod != null){
                editedReview.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(reviewNode).child(id).setValue(modelMapper.map(editedReview, Review.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        review = editedReview;
                    }
                    else{
                        review = null;
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

        return review;
    }

    @Override
    public Boolean delete(String id) {
        reviewDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(reviewNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(Review.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                reviewDeleted = true;
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

        return reviewDeleted;
    }


}
