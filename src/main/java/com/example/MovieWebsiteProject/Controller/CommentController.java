package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Comment;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.CommentService;
import com.example.MovieWebsiteProject.dto.request.CommentRequest;
import com.example.MovieWebsiteProject.dto.request.CommentUpdateRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.CommentResponse;
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


}
