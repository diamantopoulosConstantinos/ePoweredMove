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

import com.google.cloud.storage.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kosdiam.epoweredmove.models.Role;
import com.kosdiam.epoweredmove.models.User;
import com.kosdiam.epoweredmove.models.dtos.RoleDto;
import com.kosdiam.epoweredmove.models.dtos.UserDto;
import com.kosdiam.epoweredmove.models.enums.UserRole;
import com.kosdiam.epoweredmove.repositories.interfaces.IUserRepository;

import jakarta.annotation.PostConstruct;

@Primary
@Repository
public class UserRepository implements IUserRepository {
    Logger logger = Logger.getLogger(UserRepository.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ModelMapper modelMapper;

    private DatabaseReference firebaseDatabase;
    private Storage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private String userNode;
    private String roleNode;

    @PostConstruct
    public void init(){
        firebaseDatabase = context.getBean(DatabaseReference.class);
        firebaseStorage = context.getBean(Storage.class);
        firebaseAuth = context.getBean(FirebaseAuth.class);
        userNode = context.getBean("userNode", String.class);
        roleNode = context.getBean("roleNode", String.class);
    }

    private UserDto user;
    private List<UserDto> users;
    private List<RoleDto> roles;
    private Boolean userDeleted;
    private Boolean userCreated;
    private Boolean validToken;
    private String createdToken;

    @Override
    public UserDto getUser(String userId){
        var done = new CountDownLatch(1);
        try {
            user = null;
            firebaseDatabase.child(userNode).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        user = modelMapper.map(dataSnapshot.getValue(User.class), UserDto.class);
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    user = null;
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

        return user;
    }

    @Override
    public List<UserDto> getAll() {
        var done = new CountDownLatch(1);
        try {
            users = new ArrayList<>();
            firebaseDatabase.child(userNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var userSnap : dataSnapshot.getChildren()){
                            users.add(modelMapper.map(userSnap.getValue(User.class), UserDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    users = null;
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

        return users;
    }

    @Override
    public List<RoleDto> getRoles(String userId) {
        var done = new CountDownLatch(1);
        try {
            roles = new ArrayList<>();
            firebaseDatabase.child(userNode).child(userId).child(roleNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        for(var roleSnap : dataSnapshot.getChildren()){
                            roles.add(modelMapper.map(roleSnap.getValue(Role.class), RoleDto.class));
                        }
                    }
                    done.countDown();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.log(Level.SEVERE, databaseError.getMessage());
                    roles = null;
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

        return roles;
    }

    @Override
    public Boolean googleSignIn(UserDto userDto) {
        var done = new CountDownLatch(1);
        try {
            userCreated = false;
            if(userDto.getId() != null){
                done.countDown();
            }
            else{
                //create or update user info
                createUserDatabase(userDto, done);
                userCreated = true;
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
        return userCreated;
    }

    @Override
    public UserDto createUserLocally(UserDto userDto){
        var done = new CountDownLatch(1);
        try{
            user = null;
            var createRequest = new UserRecord.CreateRequest();
            createRequest.setEmail(userDto.getEmail());
            createRequest.setPassword(userDto.getPassword());
            createRequest.setDisplayName(userDto.getName());

            var createUserResponse = firebaseAuth.createUserAsync(createRequest);
            createUserResponse.addListener(() -> {}, command -> {
                try {
                    userDto.setPassword(null);
                    user = userDto;
                    user.setId(createUserResponse.get().getUid());
                    createUserDatabase(user, done);
                } catch (Exception e) {
                    user = null;
                    e.printStackTrace();
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
        return user;
    }


    private void createUserDatabase(UserDto user, CountDownLatch prevDone) {
        var done = new CountDownLatch(1);
        try {
            var roles = new ArrayList<RoleDto>();
            var userRole = new RoleDto();
            userRole.setName(UserRole.USER);
            roles.add(userRole);
            user.setRoles(roles);
            firebaseDatabase.child(userNode).child(user.getId()).setValue(modelMapper.map(user, User.class), (databaseError, databaseReference) -> {
                if(databaseError != null) {
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
            prevDone.countDown();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Boolean deleteUser(String id) {
        var done = new CountDownLatch(1);
        try{
            userDeleted = false;
            var deleteUserResponse = firebaseAuth.deleteUserAsync(id);
            deleteUserResponse.addListener(() -> {}, command -> {
                try {
                    deleteUserResponse.get();
                    deleteUserDatabase(id, done);
                    userDeleted = true;
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
        return userDeleted;
    }

    private void deleteUserDatabase(String id, CountDownLatch prevDone) {
        var done = new CountDownLatch(1);
        try {
            firebaseDatabase.child(userNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.getValue(User.class) != null) {
                        dataSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if(databaseError != null){
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
            prevDone.countDown();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//    @Override
//    public String createToken(String clientType) {
//        var done = new CountDownLatch(1);
//        try{
//            createdToken = null;
//            var randomUUID = UUID.randomUUID();
//            var additionalClaims = new HashMap<String, Object>();
//            additionalClaims.put("clientType", clientType);
//            var customTokenResponse = firebaseAuth.createCustomTokenAsync(randomUUID.toString(), additionalClaims);
//            customTokenResponse.addListener(() -> {}, command -> {
//                try {
//                    done.countDown();
//                    createdToken = customTokenResponse.get();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            done.countDown();
//        }
//
//        // wait for firebase API response
//        try{
//            done.await();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return createdToken;
//    }

    public Boolean verifyIdToken(String tokenId){
        var done = new CountDownLatch(1);
        try{
            validToken = false;
            if(tokenId == null){
                throw new Exception("Empty Token Id");
            }
            var firebaseTokenApiFuture = firebaseAuth.verifyIdTokenAsync(tokenId);
            firebaseTokenApiFuture.addListener(() -> {}, command -> {
                try {
                    firebaseTokenApiFuture.get();
                    validToken = true;
                    done.countDown();
                } catch (Exception e) {
                    done.countDown();
                    throw new RuntimeException(e);
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
        return validToken;
    }

    @Override
    public List<RoleDto> updateRoles(String userId, List<RoleDto> editedRoles){
        var done = new CountDownLatch(1);
        try {
            roles = new ArrayList<>();
            var currentUser = getUser(userId);
            if(currentUser != null){
                var mappedRoles = new ArrayList<Role>();
                for(var roleDto : editedRoles){
                    mappedRoles.add(modelMapper.map(roleDto, Role.class));
                }
                firebaseDatabase.child(userNode).child(userId).child(roleNode).setValue(mappedRoles, (databaseError, databaseReference) -> {
                    if(databaseError == null) {
                        roles = editedRoles;
                    }
                    else{
                        roles = null;
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

        return roles;
    }
}
