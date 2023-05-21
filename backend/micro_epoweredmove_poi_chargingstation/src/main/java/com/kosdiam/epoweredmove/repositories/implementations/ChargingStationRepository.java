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
import com.kosdiam.epoweredmove.models.ChargingStation;
import com.kosdiam.epoweredmove.models.dtos.ChargingStationDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IChargingStationRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class ChargingStationRepository implements IChargingStationRepository {
    Logger logger = Logger.getLogger(ChargingStationRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private Storage firebaseStorage;
    private String chargingStationNode;
    private String storageBucket;
    private String chargingStationStorageNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        firebaseStorage = context.getBean(Storage.class);
        chargingStationNode = context.getBean("chargingStationNode", String.class);
        storageBucket = context.getBean("storageBucket", String.class);
        chargingStationStorageNode = context.getBean("chargingStationStorageNode", String.class);
    }

    private ChargingStationDto chargingStation;
    private List<ChargingStationDto> chargingStations;
    private Boolean chargingStationDeleted;

    @Override
    public ChargingStationDto create(ChargingStationDto newChargingStation) {
        var done = new CountDownLatch(1);
        try {
            chargingStation = null;
            var newKey = firebaseDatabase.push().getKey();
            newChargingStation.setId(newKey);
            firebaseDatabase.child(chargingStationNode).child(newKey).setValue(modelMapper.map(newChargingStation, ChargingStation.class), (databaseError, databaseReference) -> {
                if(databaseError == null) {
                    chargingStation = newChargingStation;
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

        return chargingStation;
    }

    @Override
    public ChargingStationDto get(String id) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(chargingStationNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null) {
                                chargingStation = modelMapper.map(dataSnapshot.getValue(ChargingStation.class), ChargingStationDto.class);
                            }
                            done.countDown();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            logger.log(Level.SEVERE, databaseError.getMessage());
                            chargingStation = null;
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

        return chargingStation;
    }

    @Override
    public List<ChargingStationDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            chargingStations = new ArrayList<>();
            firebaseDatabase.child(chargingStationNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var chargingStationSnap : dataSnapshot.getChildren()){
                            chargingStations.add(modelMapper.map(chargingStationSnap.getValue(ChargingStation.class), ChargingStationDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    chargingStations = null;
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

        return chargingStations;
    }

    @Override
    public ChargingStationDto update(ChargingStationDto editedChargingStation) {
        var done = new CountDownLatch(1);
        try {
            chargingStation = null;
            var currentChargingStation = get(editedChargingStation.getId());
            if(currentChargingStation != null){
                editedChargingStation.setId(currentChargingStation.getId());
                firebaseDatabase.child(chargingStationNode).child(editedChargingStation.getId()).setValue(modelMapper.map(editedChargingStation, ChargingStation.class), (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        chargingStation = editedChargingStation;
                    }
                    else{
                        chargingStation = null;
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

        return chargingStation;
    }

    @Override
    public Boolean delete(String id) {
        chargingStationDeleted = false;
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(chargingStationNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(ChargingStation.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError == null){
                                chargingStationDeleted = true;
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

        return chargingStationDeleted;
    }

    public String addChargingStationImage(MultipartFile file){
        try{
            if(file.isEmpty()){
                throw new Exception("File is empty");
            }
            if(!Objects.equals(file.getContentType(), "image/jpeg") &&
                    !Objects.equals(file.getContentType(), "image/png")){
                throw new Exception("Wrong input file type");
            }

            var newKey = firebaseDatabase.push().getKey();
            BlobId blobId = BlobId.of(storageBucket, chargingStationStorageNode + "/" + newKey);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            firebaseStorage.create(blobInfo, file.getBytes(), 0, file.getBytes().length);
            return newKey;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean deleteChargingStationImage(String id){
        try{
            BlobId blobId = BlobId.of(storageBucket, chargingStationStorageNode + "/" + id);
            return firebaseStorage.delete(blobId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
