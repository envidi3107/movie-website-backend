package com.example.MovieWebsiteProject.dto.response;

import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotification;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String description;
    private String posterUrl;
    private String actionUrl;
}
