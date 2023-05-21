package com.kosdiam.epoweredmove.repositories.implementations;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kosdiam.epoweredmove.models.PlugType;
import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IPlugTypeRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class PlugTypeRepository implements IPlugTypeRepository {
    Logger logger = Logger.getLogger(PlugTypeRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private Storage firebaseStorage;
    private String storageBucket;
    private String plugStorageNode;
    private String plugTypeNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        plugTypeNode = context.getBean("plugTypeNode", String.class);
        firebaseStorage = context.getBean(Storage.class);
        storageBucket = context.getBean("storageBucket", String.class);
        plugStorageNode = context.getBean("plugTypeStorageNode", String.class);
    }

    private PlugTypeDto plugType;
    private List<PlugTypeDto> plugTypes;
    private Boolean plugTypeDeleted;

    @Override
    public PlugTypeDto create(PlugTypeDto newPlugType) {
        var done = new CountDownLatch(1);
        try {
            plugType = null;
            var newKey = firebaseDatabase.push().getKey();
            newPlugType.setId(newKey);
            firebaseDatabase.child(plugTypeNode).child(newKey).setValue(modelMapper.map(newPlugType, PlugType.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    plugType = newPlugType;
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

        return plugType;
    }

    @Override
    public PlugTypeDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(plugTypeNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        plugType = modelMapper.map(dataSnapshot.getValue(PlugType.class), PlugTypeDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plugType = null;
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

        return plugType;
    }

    @Override
    public List<PlugTypeDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            plugTypes = new ArrayList<>();
            firebaseDatabase.child(plugTypeNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var paymentMethodSnap : dataSnapshot.getChildren()){
                            plugTypes.add(modelMapper.map(paymentMethodSnap.getValue(PlugType.class), PlugTypeDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    plugTypes = null;
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

        return plugTypes;
    }

    @Override
    public PlugTypeDto update(PlugTypeDto editedPlugType) {
        var done = new CountDownLatch(1);
        try {
            plugType = null;
            var currentPaymentMethod = get(editedPlugType.getId());
            if(currentPaymentMethod != null){
                editedPlugType.setId(currentPaymentMethod.getId());
                firebaseDatabase.child(plugTypeNode).child(editedPlugType.getId()).setValue(modelMapper.map(editedPlugType, PlugType.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        plugType = editedPlugType;
                    }
                    else{
                        plugType = null;
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

        return plugType;
    }

    @Override
    public Boolean delete(String id) {
        plugTypeDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(plugTypeNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(PlugType.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                plugTypeDeleted = true;
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

        return plugTypeDeleted;
    }

    @Override
    public String addPlugTypeImage(MultipartFile file){
        try{
            if(file.isEmpty()){
                throw new Exception("File is empty");
            }
            if(!Objects.equals(file.getContentType(), "image/jpeg") &&
                    !Objects.equals(file.getContentType(), "image/png")){
                throw new Exception("Wrong input file type");
            }

            var newKey = firebaseDatabase.push().getKey();
            BlobId blobId = BlobId.of(storageBucket, plugStorageNode + "/" + newKey);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            firebaseStorage.create(blobInfo, file.getBytes(), 0, file.getBytes().length);
            return newKey;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean deletePlugTypeImage(String id){
        try{
            BlobId blobId = BlobId.of(storageBucket, plugStorageNode + "/" + id);
            return firebaseStorage.delete(blobId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
