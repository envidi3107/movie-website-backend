package com.example.IdentityService.dto.projection;

import java.time.LocalDateTime;

public interface FilmComments {
    String getCommentId();
    String getUserId();
    String getContent();
    LocalDateTime getCommentTime();
}
