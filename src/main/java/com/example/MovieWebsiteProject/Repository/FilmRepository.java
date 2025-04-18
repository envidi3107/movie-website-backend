package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, String> {

    Optional<Film> findById(String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_views = number_of_views + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseView(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_likes = number_of_likes + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseLike(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_dislikes = number_of_dislikes + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseDislike(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_comments = number_of_comments + 1 " +
            "WHERE film_id = :filmId", nativeQuery = true)
    void increaseComment(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_likes = number_of_likes - 1 " +
            "WHERE film_id = :filmId AND number_of_likes > 0", nativeQuery = true)
    void decreaseLike(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_dislikes = number_of_dislikes - 1 " +
            "WHERE film_id = :filmId AND number_of_dislikes > 0", nativeQuery = true)
    void decreaseDislike(@Param("filmId") String filmId);

    @Modifying
    @Query(value = "UPDATE film " +
            "SET number_of_comments = number_of_comments - 1 " +
            "WHERE film_id = :filmId AND number_of_comments > 0", nativeQuery = true)
    void decreaseComment(@Param("filmId") String filmId);

}
