package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.UserService;
import com.example.MovieWebsiteProject.dto.request.PasswordUpdateRequest;
import com.example.MovieWebsiteProject.dto.request.UserCreationRequest;
import com.example.MovieWebsiteProject.dto.request.UserUpdateRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    AuthenticationService authenticationService;

    @PostMapping("/signup")
    ApiResponse<Void> creteUser(@Valid @RequestBody UserCreationRequest request, HttpServletRequest httpServletRequest) {
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
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createAt(user.getCreatedAt())
                .avatarPath(user.getAvatarPath())
                .role(user.getRole())
                .dateOfBirth(user.getDateOfBirth())
                .build();
        return ApiResponse.<UserResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(userResponse)
                .build();
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<User> updateUser(
            @Valid @ModelAttribute UserUpdateRequest request
    ) {
        userService.updateUser(request);

        return ApiResponse.<User>builder()
                .code(SuccessCode.UPDATED_SUCCESSFULLY.getCode())
                .message(SuccessCode.UPDATED_SUCCESSFULLY.getMessage())
                .build();
    }


    @DeleteMapping("/delete/{userId}")
    ApiResponse<User> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setCode(SuccessCode.DELETED_SUCCESSFULLY.getCode());
        apiResponse.setMessage(SuccessCode.DELETED_SUCCESSFULLY.getMessage());
        return apiResponse;
    }

}
