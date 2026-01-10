package com.example.MovieWebsiteProject.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Dto.projection.FilmComments;
import com.example.MovieWebsiteProject.Entity.Comment.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

  @Query(
      value =
          """
            SELECT count(user_id)
            FROM comment
            WHERE user_id = :userId AND film_id = :filmId
    """,
      nativeQuery = true)
  int countUserCommentFilm(@Param("userId") String userId, @Param("filmId") String filmId);

  @Query(
      value =
          """
            SELECT c.comment_id, c.user_id, u.username, u.avatar_path, c.content, c.comment_time
            FROM comment c
            JOIN user u ON c.user_id = u.id
            WHERE c.film_id = :filmId ORDER BY c.comment_time DESC
    """,
      nativeQuery = true)
  List<FilmComments> getFilmComments(@Param("filmId") String filmId);

  List<Comment> findByFilm_FilmIdAndParentCommentIsNullOrderByCommentTimeDesc(String filmId);
}
