package com.example.MovieWebsiteProject.Entity.UserNotification;

import com.example.MovieWebsiteProject.Entity.Notification;
import com.example.MovieWebsiteProject.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserNotification {
    @EmbeddedId
    private UserNotificationID id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    public UserNotification(User user, Notification notification, LocalDateTime notifiedAt) {
        this.id = new UserNotificationID(user.getId(), notification.getId());
        this.user = user;
        this.notification = notification;
        this.notifiedAt = notifiedAt;
    }
}
