package com.example.MovieWebsiteProject.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private String commentId;
    private String username;
    private String avatarPath;
    private String content;
    private LocalDateTime commentTime;
    private List<CommentResponse> childComments;
}

