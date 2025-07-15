package com.example.MovieWebsiteProject.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SYSTEM_FILM")
public class SystemFilm {
    @Id
    @Column(name = "system_film_id")
    private String systemFilmId;

    @Column(name = "adult")
    private boolean adult;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "total_durations")
    private double totalDurations;

    @Column(name = "is_use_src", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isUseSrc;

    @OneToMany(mappedBy = "systemFilm", cascade = CascadeType.ALL)
    private Set<Episode> episodes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "system_film_genres",
            joinColumns = @JoinColumn(name = "system_film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToOne
    @MapsId
    @JoinColumn(name = "system_film_id")
    private Film film;
}
