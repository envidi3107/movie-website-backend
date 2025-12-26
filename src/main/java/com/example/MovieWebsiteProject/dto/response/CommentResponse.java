package com.example.MovieWebsiteProject.Dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private String commentId;
    private String userId;
    private String username;
    private String avatarPath;
    private String content;
    private LocalDateTime commentTime;
    private List<CommentResponse> childComments;
}

