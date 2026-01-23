package com.example.MovieWebsiteProject.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotification;
import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotificationID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationID> {

    void deleteByUser_IdAndNotification_Id(String userId, Long notificationId);

    List<UserNotification> findAllByUser_Id(String userId);

    void deleteAllByUser_Id(String userId);
}
