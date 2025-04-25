package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.SystemFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SystemFilmRepository extends JpaRepository<SystemFilm, String> {

    @Query(value = "SELECT sf.system_film_id, sf.title, sf.release_date, sf.backdrop_path, sf.poster_path, genre.genre_name FROM system_film AS sf JOIN system_film_genres AS sfg ON sf.system_film_id = sfg.system_film_id JOIN genre ON sfg.genre_id = genre.genre_id ORDER BY sf.release_date DESC", nativeQuery = true)
    List<Map<String, Object>> getAllSystemFilmSummary();

    @Query(value = "SELECT sf.*, film.*, genre.genre_name\n" +
            "FROM system_film AS sf\n" +
            "JOIN film ON film.film_id = sf.system_film_id\n" +
            "JOIN system_film_genres AS sfg ON sf.system_film_id = sfg.system_film_id\n" +
            "JOIN genre ON sfg.genre_id = genre.genre_id\n" +
            "WHERE sf.system_film_id = :filmId", nativeQuery = true)
    List<Map<String, Object>> getSystemFilmDetail(@Param("filmId") String filmId);
}
