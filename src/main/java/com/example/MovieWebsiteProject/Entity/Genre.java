package com.example.MovieWebsiteProject.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private int genreId;

    @Column(name = "genre_name", unique = true)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    private Set<SystemFilm> systemFilms = new HashSet<>();

    public Genre(String name, SystemFilm systemFilm) {
        this.genreName = name.substring(0, 1).toUpperCase() + name.substring(1);
        this.systemFilms.add(systemFilm);
    }
}
