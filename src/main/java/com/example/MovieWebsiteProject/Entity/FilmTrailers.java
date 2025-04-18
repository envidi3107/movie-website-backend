package com.example.IdentityService.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "film_trailers")
public class FilmTrailers {
    @Id
    @Column(name = "film_trailers_id")
    private String filmTrailersId;

    @Column(name = "video_key")
    private String videoKey;

    @Column(name = "tmdb_id")
    private long tmdbId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "film_trailers_id", referencedColumnName = "film_id")
    private Film film;
}
