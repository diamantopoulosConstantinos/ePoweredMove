package com.kosdiam.epoweredmove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosdiam.epoweredmove.models.dtos.RoleDto;
import com.kosdiam.epoweredmove.models.dtos.UserDto;
import com.kosdiam.epoweredmove.repositories.interfaces.IUserRepository;

@Service
public class UserService {
    Logger logger = Logger.getLogger(UserService.class.getName());
    @Autowired
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUser(String userId){
        if(userId == null){
            logger.log(Level.WARNING, "user id is null");
            return null;
        }
        var userFound = userRepository.getUser(userId);
        if(userFound == null){
            logger.log(Level.WARNING, "user is null");
            return null;
        }
//        var userRoles = getRoles(userId);
//        if(userRoles == null){
//            logger.log(Level.WARNING, "user roles is null");
//            return null;
//        }
//        userFound.setRoles(userRoles);
        return userFound;
    }

    public List<UserDto> getUsers(){
        var usersFound = userRepository.getAll();
        if(usersFound == null){
            logger.log(Level.WARNING, "users is null");
            return null;
        }
        var completeUsers = new ArrayList<UserDto>();
        for(var user : usersFound){
            var completeUser = getUser(user.getId());
            if(completeUser != null){
                completeUsers.add(completeUser);
            }
        }
        return completeUsers;
    }

    public UserDto createUserLocally(UserDto userDto){
        return userRepository.createUserLocally(userDto);
    }

    public Boolean googleSignIn(UserDto userDto){
        return userRepository.googleSignIn(userDto);
    }
    public Boolean deleteUser(String userId){
        return userRepository.deleteUser(userId);
    }

    public Boolean verifyToken(String tokenId){
        return userRepository.verifyIdToken(tokenId);
    }

    public List<RoleDto> getRoles(String userId) {
        return userRepository.getRoles(userId);
    }

    public List<RoleDto> updateRoles(String userId, List<RoleDto> roles) {
        return userRepository.updateRoles(userId, roles);
    }
}
