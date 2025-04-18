package com.example.IdentityService.mapper;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.dto.request.UserCreationRequest;
import com.example.IdentityService.dto.request.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
