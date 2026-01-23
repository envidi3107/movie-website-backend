package com.example.MovieWebsiteProject.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.MovieWebsiteProject.Dto.response.NotificationResponse;
import com.example.MovieWebsiteProject.Entity.Notification;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotification;
import com.example.MovieWebsiteProject.Repository.UserNotificationRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserNotificationService {
    UserRepository userRepository;
    UserNotificationRepository userNotificationRepository;

    public void saveAllUserNotification(Notification notification) {
        List<User> users = userRepository.findAll();
        List<UserNotification> userNotifications = new ArrayList<>();
        for (User user : users) {
            if (!user.getRole().equals("ADMIN")) {
                UserNotification userNotification = new UserNotification(user, notification, LocalDateTime.now());
                userNotifications.add(userNotification);
            }
        }
        userNotificationRepository.saveAll(userNotifications);
    }

    public List<NotificationResponse> getAllUserNotification(String userId) {
        List<UserNotification> userNotifications = userNotificationRepository.findAllByUser_Id(userId);
        List<NotificationResponse> responses = new ArrayList<>();
        for (UserNotification userNotification : userNotifications) {
            NotificationResponse notificationResponse = NotificationResponse.builder().id(userNotification.getNotification().getId()).title(userNotification.getNotification().getTitle()).description(userNotification.getNotification().getDescription()).posterUrl(userNotification.getNotification().getPosterUrl()).actionUrl(userNotification.getNotification().getActionUrl()).build();

            responses.add(notificationResponse);
        }

        return responses;
    }

    public void deleteUserNotification(String userId, Long notificationId) {
        userNotificationRepository.deleteByUser_IdAndNotification_Id(userId, notificationId);
    }

    public void clearAllUserNotification(String userId) {
        userNotificationRepository.deleteAllByUser_Id(userId);
    }
}
