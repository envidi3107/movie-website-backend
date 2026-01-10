package com.example.MovieWebsiteProject.Entity.UserNotification;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserNotificationID implements Serializable {
  @Column(name = "user_id")
  private String userId;

  @Column(name = "notification_id")
  private Long notificationId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserNotificationID that = (UserNotificationID) o;
    return Objects.equals(userId, that.userId)
        && Objects.equals(notificationId, that.notificationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, notificationId);
  }
}
