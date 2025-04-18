package com.example.IdentityService.Entity;

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

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<SystemFilm> films = new HashSet<>();

    public Genre(String name) {
        this.name = name;
    }
}
