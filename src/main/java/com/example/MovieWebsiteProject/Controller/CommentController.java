package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Comment.Comment;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.CommentService;
import com.example.MovieWebsiteProject.Service.EpisodeCommentService;
import com.example.MovieWebsiteProject.Dto.request.CommentRequest;
import com.example.MovieWebsiteProject.Dto.request.CommentUpdateRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.CommentResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    AuthenticationService authenticationService;
    CommentRepository commentRepository;
    CommentService commentService;
    FilmRepository filmRepository;
    UserRepository userRepository;
    EpisodeCommentService episodeCommentService;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-comment")
    public ApiResponse<CommentResponse> saveComment(@RequestBody CommentRequest commentRequest) {

        return ApiResponse.<CommentResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(commentService.saveComment(commentRequest))
                .build();
    }

    @PostMapping("/update-comment")
    public ApiResponse<Void> updateUserComment(@RequestBody CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(request.getContent());
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

    @GetMapping("/film/{filmId}/comment-list")
    public ResponseEntity<List<CommentResponse>> getCommentsByFilmId(@PathVariable("filmId") String filmId) {
        List<CommentResponse> comments = commentService.getCommentsByFilmId(filmId);
        return ResponseEntity.ok(comments);
    }

    // Episode comments
    @PostMapping("/episode/save-comment")
    public ApiResponse<CommentResponse> saveEpisodeComment(@RequestParam("episodeId") String episodeId, @RequestParam(value = "parentCommentId", required = false) String parentCommentId, @RequestParam("content") String content) {
        CommentResponse res = episodeCommentService.saveComment(episodeId, parentCommentId, content);
        return ApiResponse.<CommentResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(res)
                .build();
    }

    @GetMapping("/episode/{episodeId}/comment-list")
    public ResponseEntity<List<CommentResponse>> getCommentsByEpisodeId(@PathVariable("episodeId") String episodeId) {
        var comments = episodeCommentService.getCommentsByEpisodeId(episodeId);
        return ResponseEntity.ok(comments);
    }

}
