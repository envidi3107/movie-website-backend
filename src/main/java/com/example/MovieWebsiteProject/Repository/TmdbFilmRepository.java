package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.TmdbFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface TmdbFilmRepository extends JpaRepository<TmdbFilm, String> {

    Optional<TmdbFilm> findByTmdbId(String tmdbId);

    @Query(value = "SELECT film.film_id, film.number_of_views, film.number_of_likes, film.number_of_dislikes, film.belong_to, tf.tmdb_id\n" +
            "FROM tmdb_film AS tf\n" +
            "JOIN film ON tf.id = film.film_id\n" +
            "WHERE tmdb_id = :tmdbId", nativeQuery = true)
    Map<String, Object> getTmdbFilmByTmdbId(@Param("tmdbId") String tmdbId);
}
