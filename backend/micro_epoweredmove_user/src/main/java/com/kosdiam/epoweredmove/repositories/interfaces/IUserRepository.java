package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.RoleDto;
import com.kosdiam.epoweredmove.models.dtos.UserDto;

@Repository
public interface IUserRepository {
    UserDto createUserLocally(UserDto user);
    Boolean deleteUser(String id);
    //String createToken(String clientType);
    Boolean verifyIdToken(String tokenId);
    UserDto getUser(String userId);

    Boolean googleSignIn(UserDto userDto);

    List<UserDto> getAll();

    List<RoleDto> getRoles(String userId);

    List<RoleDto> updateRoles(String userId, List<RoleDto> roles);
}
