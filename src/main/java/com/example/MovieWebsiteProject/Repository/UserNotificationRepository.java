package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Notification;
import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotification;
import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotificationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationID> {

    void deleteByUser_IdAndNotification_Id(String userId, Long notificationId);

    List<UserNotification> findAllByUser_Id(String userId);

    void deleteAllByUser_Id(String userId);
}
