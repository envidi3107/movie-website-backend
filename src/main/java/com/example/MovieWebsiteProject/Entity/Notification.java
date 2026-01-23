package com.example.MovieWebsiteProject.Entity;

import java.util.List;

import jakarta.persistence.*;

import com.example.MovieWebsiteProject.Entity.UserNotification.UserNotification;

import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "action_url")
    private String actionUrl;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
    private List<UserNotification> userNotification;
}
