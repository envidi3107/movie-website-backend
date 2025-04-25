package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, String> {

    Optional<Film> findById(String filmId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE film " +
            "SET number_of_views = number_of_views + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseView(@Param("filmId") String filmId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE film " +
            "SET number_of_comments = number_of_comments + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseComment(@Param("filmId") String filmId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE film " +
            "SET number_of_comments = number_of_comments - 1 " +
            "WHERE film_id = :filmId AND number_of_comments > 0", nativeQuery = true)
    void decreaseComment(@Param("filmId") String filmId);

    @Query(value = "SELECT * FROM film ORDER BY number_of_views DESC LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> getFilmDetail();

}
