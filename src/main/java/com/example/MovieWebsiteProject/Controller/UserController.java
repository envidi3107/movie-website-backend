package com.example.MovieWebsiteProject.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.MovieWebsiteProject.Dto.request.PasswordUpdateRequest;
import com.example.MovieWebsiteProject.Dto.request.UserCreationRequest;
import com.example.MovieWebsiteProject.Dto.request.UserUpdateRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserService userService;

  @Value("${app.base_url}")
  @NonFinal
  private String base_url;

  @PostMapping("/signup")
  ApiResponse<Void> creteUser(
      @Valid @RequestBody UserCreationRequest request, HttpServletRequest httpServletRequest) {
    userService.createUser(request, httpServletRequest);

    return ApiResponse.<Void>builder()
        .code(SuccessCode.SIGN_UP_SUCCESSFULLY.getCode())
        .message(SuccessCode.SIGN_UP_SUCCESSFULLY.getMessage())
        .build();
  }

  @PostMapping("/update-password")
  ApiResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
    userService.updateUserPassword(request.getPassword());
    return ApiResponse.<Void>builder()
        .code(SuccessCode.UPDATED_PASSWORD_SUCCESSFULLY.getCode())
        .message(SuccessCode.UPDATED_PASSWORD_SUCCESSFULLY.getMessage())
        .build();
  }

  @GetMapping("/my-info")
  ApiResponse<UserResponse> getUserInfo(HttpServletRequest request) {

    User user = userService.getUserInfo();
    UserResponse userResponse =
        UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .avatarPath(base_url + user.getAvatarPath())
            .role(user.getRole())
            .dateOfBirth(user.getDateOfBirth())
            .build();
    return ApiResponse.<UserResponse>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .results(userResponse)
        .build();
  }

  @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<UserResponse> updateUser(@Valid @ModelAttribute UserUpdateRequest request) {

    return ApiResponse.<UserResponse>builder()
        .code(SuccessCode.UPDATED_SUCCESSFULLY.getCode())
        .message(SuccessCode.UPDATED_SUCCESSFULLY.getMessage())
        .results(userService.updateUser(request))
        .build();
  }
}
