package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Service.AdminService;
import com.example.IdentityService.Service.AuthenticationService;
import com.example.IdentityService.Service.UserService;
import com.example.IdentityService.dto.request.PasswordUpdateRequest;
import com.example.IdentityService.dto.response.ApiResponse;
import com.example.IdentityService.dto.request.UserCreationRequest;
import com.example.IdentityService.dto.request.UserUpdateRequest;
import com.example.IdentityService.dto.response.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(SuccessCode.SIGN_UP_SUCCESSFULLY.getCode());
        apiResponse.setMessage(SuccessCode.SIGN_UP_SUCCESSFULLY.getMessage());
        return apiResponse;
    }

    @PostMapping("/update-password/{userId}")
    ApiResponse<Void> updatePassword(@PathVariable("userId") String userId, @RequestBody PasswordUpdateRequest request) {
        userService.updateUserPassword(userId, request.getPassword());
        return ApiResponse.<Void>builder()
                .code(SuccessCode.UPDATED_PASSWORD_SUCCESSFULLY.getCode())
                .message(SuccessCode.UPDATED_PASSWORD_SUCCESSFULLY.getMessage())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<User> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<User>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getUserInfo(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            System.out.println("cookie request = " + cookie.getName() + ", " + cookie.getValue());
        }

        User user = userService.getUserInfo();
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createAt(user.getCreatedAt())
                .avatarName(user.getAvatarName())
                .avatarData(user.getAvatarData())
                .build();
        ApiResponse apiResponse = ApiResponse.<UserResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(userResponse)
                .build();
        return apiResponse;
    }

    @PutMapping("/update/{userId}")
    ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        userService.updateUser(userId, request);

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setCode(SuccessCode.UPDATED_SUCCESSFULLY.getCode());
        apiResponse.setMessage(SuccessCode.UPDATED_SUCCESSFULLY.getMessage());
        return apiResponse;
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
