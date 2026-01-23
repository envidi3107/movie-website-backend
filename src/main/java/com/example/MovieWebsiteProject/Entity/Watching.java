package com.example.MovieWebsiteProject.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Watching {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String watchId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "watching_time")
    private LocalDateTime watchTime;

    @Column(name = "watching_hour")
    private int watchHour;

    @Column(name = "watched_duration")
    private long watchedDuration;
}
