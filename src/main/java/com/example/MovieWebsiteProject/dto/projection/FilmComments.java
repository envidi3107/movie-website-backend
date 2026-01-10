package com.example.MovieWebsiteProject.Dto.projection;

import java.time.LocalDateTime;

public interface FilmComments {
  String getCommentId();

  String getUserId();

  String getUserName();

  String getAvatarPath();

  String getContent();

  LocalDateTime getCommentTime();
}
