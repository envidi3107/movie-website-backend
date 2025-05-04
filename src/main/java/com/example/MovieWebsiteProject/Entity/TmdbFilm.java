package com.example.MovieWebsiteProject.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tmdb_film")
public class TmdbFilm {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "tmdb_id")
    private String tmdbId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Film film;
}
