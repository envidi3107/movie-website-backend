package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.UserNotificationService;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.NotificationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserNotificationController {
    AuthenticationService authenticationService;
    UserNotificationService userNotificationService;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @GetMapping("/get-all")
    public ApiResponse<List<NotificationResponse>> getAllUserNotification() {

        return ApiResponse.<List<NotificationResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(userNotificationService.getAllUserNotification(getAuthUserId()))
                .build();
    }

    @Transactional
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteUserNotification(@RequestParam("notificationId") Long notificationId) {
        userNotificationService.deleteUserNotification(getAuthUserId(), notificationId);

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @Transactional
    @DeleteMapping("/clear-all")
    public ApiResponse<Void> clearAllUserNotification() {
        userNotificationService.clearAllUserNotification(getAuthUserId());

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }
}
