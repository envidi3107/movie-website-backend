package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Comment;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.CommentService;
import com.example.MovieWebsiteProject.dto.request.CommentRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.CommentResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ApiResponse<Void> saveUserComment(@RequestBody CommentRequest request) {
        commentService.saveComment(request.getFilmId(), getAuthUserId(), request.getContent());

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/update-comment")
    public ApiResponse<Void> updateUserComment(@RequestBody CommentRequest request) {
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));
        User user = userRepository.findById(getAuthUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment.CommentBuilder commentBuilder = Comment.builder()
                .user(user)
                .film(film)
                .content(request.getContent())
                .commentTime(LocalDateTime.now());

        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            commentBuilder.parentComment(parentComment);
        } else {
            commentBuilder.parentComment(null);
        }

        Comment comment = commentBuilder.build();
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

//    @GetMapping("/get-film-comments/{filmId}")
//    public ApiResponse<List<Map<String, String>>> getFilmComment(@PathVariable("filmId") String filmId) {
//        List<FilmComments> results = commentRepository.getFilmComments(filmId);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
//        List<Map<String, String>> response = new ArrayList<>();
//        results.forEach(comment -> {
//            Map<String, String> data = new HashMap<>();
//            data.put("comment_id", comment.getCommentId());
//            data.put("user_id", comment.getUserId());
//            data.put("username", comment.getUserName());
//            data.put("avatar_path", comment.getAvatarPath());
//            data.put("content", comment.getContent());
//            data.put("comment_time", comment.getCommentTime().format(formatter));
//            response.add(data);
//        });
//        return ApiResponse.<List<Map<String, String>>>builder()
//                .code(SuccessCode.SUCCESS.getCode())
//                .message(SuccessCode.SUCCESS.getMessage())
//                .results(response)
//                .build();
//    }

    @GetMapping("/film/{filmId}/comment-list")
    public ResponseEntity<List<CommentResponse>> getCommentsByFilmId(@PathVariable("filmId") String filmId) {
        List<CommentResponse> comments = commentService.getCommentsByFilmId(filmId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/reply")
    public ApiResponse<Void> replyComment(@RequestBody CommentRequest commentRequest) {
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Film film = filmRepository.findById(commentRequest.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = Comment.builder()
                .user(user)
                .film(film)
                .parentComment(parentComment)
                .content(commentRequest.getContent())
                .commentTime(LocalDateTime.now())
                .build();

        commentRepository.save(reply);
        filmRepository.increaseComment(film.getFilmId());
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }
}
