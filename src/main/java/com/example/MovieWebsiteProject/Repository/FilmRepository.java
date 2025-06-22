package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface FilmRepository extends JpaRepository<Film, String> {

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE film
            SET number_of_views = number_of_views + 1
            WHERE film_id = :filmId
            """, nativeQuery = true)
    void increaseView(@Param("filmId") String filmId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE film
            SET number_of_comments = number_of_comments + 1
            WHERE film_id = :filmId
            """, nativeQuery = true)
    void increaseComment(@Param("filmId") String filmId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE film
            SET number_of_comments = number_of_comments - 1
            WHERE film_id = :filmId AND number_of_comments > 0
            """, nativeQuery = true)
    void decreaseComment(@Param("filmId") String filmId);

    @Query(value = """
            SELECT film_id, belong_to, number_of_views, sf.title, sf.backdrop_path, sf.poster_path, sf.release_date, tf.tmdb_id
            FROM film
            LEFT JOIN system_film AS sf ON film.film_id = sf.system_film_id
            LEFT JOIN tmdb_film AS tf ON film.film_id = tf.id
            ORDER BY number_of_views DESC
            LIMIT :size
            """, nativeQuery = true)
    List<Map<String, Object>> getTopViewFilms(@Param("size") int size);

    @Query(value = """
            SELECT film_id, belong_to, number_of_likes, sf.title, sf.backdrop_path, sf.poster_path, sf.release_date, tf.tmdb_id
            FROM film
            LEFT JOIN system_film AS sf ON film.film_id = sf.system_film_id
            LEFT JOIN tmdb_film AS tf ON film.film_id = tf.id
            ORDER BY number_of_likes DESC
            LIMIT :size
            """, nativeQuery = true)
    List<Map<String, Object>> getTopLikeFilms(@Param("size") int size);
}
