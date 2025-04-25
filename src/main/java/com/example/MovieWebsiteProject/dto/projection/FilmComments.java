package com.example.MovieWebsiteProject.dto.projection;

import java.time.LocalDateTime;

public interface FilmComments {
    String getCommentId();

    String getUserId();

    String getUserName();

    String getAvatarPath();

    String getContent();

    LocalDateTime getCommentTime();
}
