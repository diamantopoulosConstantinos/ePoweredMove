package com.kosdiam.epoweredmove.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosdiam.epoweredmove.models.dtos.RoleDto;
import com.kosdiam.epoweredmove.models.dtos.UserDto;
import com.kosdiam.epoweredmove.services.UserService;
import com.kosdiam.epoweredmove.utils.exceptions.RecordNotFoundException;

@RestController
@RequestMapping("epoweredmove/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUser(@RequestParam String id) {
        var user = userService.getUser(id);
        if(user == null) {
            throw new RecordNotFoundException("user not exist");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(path = "all", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getUsers() {
        var users = userService.getUsers();
        if(users == null) {
            throw new RecordNotFoundException("Users error occurred");
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(path = "roles", method = RequestMethod.GET)
    public ResponseEntity<List<RoleDto>> getRoles(@RequestParam String id) {
        var roles = userService.getRoles(id);
        if(roles == null) {
            throw new RecordNotFoundException("User roles error occurred");
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @RequestMapping(path = "roles", method = RequestMethod.PUT)
    public ResponseEntity<List<RoleDto>> updateRoles(@RequestParam String id, @RequestBody List<RoleDto> roles) {
        var updatedRoles = userService.updateRoles(id, roles);
        if(updatedRoles == null) {
            throw new RecordNotFoundException("User roles update error occurred");
        }
        return new ResponseEntity<>(updatedRoles, HttpStatus.OK);
    }

    @RequestMapping(path = "createUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createUserLocally(@RequestBody UserDto userRequest) {
        var user = userService.createUserLocally(userRequest);
        if(user == null) {
            throw new RecordNotFoundException("user not created");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(path = "googleSignIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> googleSignIn(@RequestBody UserDto userRequest) {
        var userCreated = userService.googleSignIn(userRequest);
        return new ResponseEntity<>(userCreated, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteUser(@RequestParam String id) {
        var userDeleted = userService.deleteUser(id);
        if(userDeleted == null) {
            throw new RecordNotFoundException("user not deleted");
        }
        return new ResponseEntity<>(userDeleted, HttpStatus.OK);
    }

    @RequestMapping(path = "verifyToken", method = RequestMethod.POST)
    public ResponseEntity<Boolean> verifyToken(@RequestHeader String authorization) {
        var verifyToken = userService.verifyToken(authorization);
        return new ResponseEntity<>(verifyToken, HttpStatus.OK);
    }
}
