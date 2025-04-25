package com.example.MovieWebsiteProject.mapper;

import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.dto.request.UserCreationRequest;
import com.example.MovieWebsiteProject.dto.request.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
