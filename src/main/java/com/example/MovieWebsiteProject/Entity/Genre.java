package com.example.MovieWebsiteProject.Entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;

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
  private Set<Film> systemFilms = new HashSet<>();
}
