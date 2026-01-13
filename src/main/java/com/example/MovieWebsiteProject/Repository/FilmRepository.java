package com.example.MovieWebsiteProject.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.MovieWebsiteProject.Entity.Film;

@Repository
public interface FilmRepository extends JpaRepository<Film, String> {

  @Modifying
  @Transactional
  @Query(
      value =
          """
            UPDATE film
            SET number_of_views = number_of_views + 1
            WHERE film_id = :filmId
            """,
      nativeQuery = true)
  void increaseView(@Param("filmId") String filmId);

  @Modifying
  @Transactional
  @Query(
      value =
          """
            UPDATE film
            SET number_of_comments = number_of_comments + 1
            WHERE film_id = :filmId
            """,
      nativeQuery = true)
  void increaseComment(@Param("filmId") String filmId);

  @Modifying
  @Transactional
  @Query(
      value =
          """
            UPDATE film
            SET number_of_comments = number_of_comments - 1
            WHERE film_id = :filmId AND number_of_comments > 0
            """,
      nativeQuery = true)
  void decreaseComment(@Param("filmId") String filmId);

  // Simple pageable search by title
  Page<Film> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

  Page<Film> findByTitleContainingIgnoreCaseAndAdult(
      @Param("title") String title, @Param("adult") Boolean adult, Pageable pageable);

  // Search by title + genres (must include all genres in the list) + optional adult flag
  @Query(
      "SELECT f FROM Film f JOIN f.genres g WHERE (:title IS NULL OR LOWER(f.title) LIKE CONCAT('%',:title,'%')) AND LOWER(g.genreName) IN :genres AND (:adult IS NULL OR f.adult = :adult) GROUP BY f HAVING COUNT(DISTINCT g) >= :genreCount")
  Page<Film> findByTitleAndGenres(
      @Param("title") String title,
      @Param("genres") List<String> genres,
      @Param("genreCount") long genreCount,
      @Param("adult") Boolean adult,
      Pageable pageable);

  @Query(
      value =
        """
            SELECT f FROM Film f
            ORDER BY f.numberOfViews DESC
        """)
  Page<Film> findTopByOrderByNumberOfViewsDesc(int q, Pageable pageable);

  List<Film> findTopByOrderByReleaseDateDesc(Pageable pageable);
}
