package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.ReactionType;
import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Entity.Comment;
import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.Reaction.Reaction;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Repository.CommentRepository;
import com.example.IdentityService.Repository.FilmRepository;
import com.example.IdentityService.Service.AuthenticationService;
import com.example.IdentityService.Service.FilmService;
import com.example.IdentityService.Service.UserService;
import com.example.IdentityService.dto.projection.FilmComments;
import com.example.IdentityService.dto.request.CommentRequest;
import com.example.IdentityService.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    AuthenticationService authenticationService;
    FilmRepository filmRepository;
    UserService userService;
    FilmService filmService;
    CommentRepository commentRepository;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-comment")
    public ApiResponse<Void> saveUserComment(@RequestBody CommentRequest request) {
        User user = userService.getUser(getAuthUserId());
        Film film = filmService.getFilmById(request.getFilmId());
        int count = commentRepository.countUserCommentFilm(getAuthUserId(), request.getFilmId());
        System.out.println("count = " + count);
        if (count <= 2) {
            Comment comment = Comment.builder()
                    .user(user)
                    .film(film)
                    .content(request.getContent())
                    .commentTime(request.getCommentTime())
                    .build();
            commentRepository.save(comment);
            filmRepository.increaseComment(film.getFilmId());
        }

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-film-comments/{filmId}")
    public ApiResponse<List<Map<String, String>>> getFilmComment(@PathVariable("filmId") String filmId) {
        List<FilmComments> results = commentRepository.getFilmComments(filmId);
        List<Map<String, String>> response = new ArrayList<>();
        results.forEach(comment -> {
            Map<String, String> data = new HashMap<>();
            data.put("comment_id", comment.getCommentId());
            data.put("user_id", comment.getUserId());
            data.put("content", comment.getContent());
            data.put("comment_time", comment.getCommentTime().toString());
            response.add(data);
        });
        return ApiResponse.<List<Map<String, String>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(response)
                .build();
    }
}
