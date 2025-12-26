package com.example.MovieWebsiteProject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    private String commentId;
    private String filmId;
    private String content;
}
