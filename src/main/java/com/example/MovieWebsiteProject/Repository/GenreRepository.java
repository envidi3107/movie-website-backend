package com.example.MovieWebsiteProject.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
  Optional<Genre> findByGenreName(String name);
}
