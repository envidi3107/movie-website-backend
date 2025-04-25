package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Comment;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.CommentService;
import com.example.MovieWebsiteProject.dto.projection.FilmComments;
import com.example.MovieWebsiteProject.dto.request.CommentRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    AuthenticationService authenticationService;
    CommentRepository commentRepository;
    CommentService commentService;
    private final FilmRepository filmRepository;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-comment")
    public ApiResponse<Void> saveUserComment(@RequestBody CommentRequest request) {
        commentService.saveComment(request.getFilmId(), getAuthUserId(), request.getContent());

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/update-comment")
    public ApiResponse<Void> updateUserComment(@RequestBody CommentRequest request) {
        Comment comment = Comment.builder()
                .content(request.getContent())
                .commentTime(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/delete-comment")
    public ApiResponse<Void> deleteUserComment(@RequestParam("commentId") String commentId, @RequestParam("filmId") String filmId) {
        commentRepository.deleteById(commentId);
        filmRepository.decreaseComment(filmId);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-film-comments/{filmId}")
    public ApiResponse<List<Map<String, String>>> getFilmComment(@PathVariable("filmId") String filmId) {
        List<FilmComments> results = commentRepository.getFilmComments(filmId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        List<Map<String, String>> response = new ArrayList<>();
        results.forEach(comment -> {
            Map<String, String> data = new HashMap<>();
            data.put("comment_id", comment.getCommentId());
            data.put("user_id", comment.getUserId());
            data.put("username", comment.getUserName());
            data.put("avatar_path", comment.getAvatarPath());
            data.put("content", comment.getContent());
            data.put("comment_time", comment.getCommentTime().format(formatter));
            response.add(data);
        });
        return ApiResponse.<List<Map<String, String>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(response)
                .build();
    }
}
