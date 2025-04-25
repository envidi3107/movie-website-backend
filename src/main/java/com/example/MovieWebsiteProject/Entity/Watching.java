package com.example.MovieWebsiteProject.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "watch_time")
    private LocalDateTime watchTime;

    @Column(name = "watch_hour")
    private int watchHour;
}
