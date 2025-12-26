package com.example.MovieWebsiteProject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String filmId;
    private String parentCommentId;
    private String content;
}
