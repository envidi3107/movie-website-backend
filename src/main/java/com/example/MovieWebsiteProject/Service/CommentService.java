package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Comment.Comment;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Dto.request.CommentRequest;
import com.example.MovieWebsiteProject.Dto.response.CommentResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    FilmRepository filmRepository;
    UserService userService;
    FilmService filmService;
    CommentRepository commentRepository;
    AuthenticationService authenticationService;

    @Value("${app.base_url}")
    @NonFinal
    String baseUrl;

    public CommentResponse saveComment(CommentRequest commentRequest) {
        User user = authenticationService.getAuthenticatedUser();

        int count = commentRepository.countUserCommentFilm(user.getId(), commentRequest.getFilmId());

        if (count <= 2) {
            Film film = filmRepository.findById(commentRequest.getFilmId()).orElseThrow(() -> new RuntimeException("Film not found"));

            Comment parentComment = null;

            if (commentRequest.getParentCommentId() != null && !commentRequest.getParentCommentId().isEmpty()) {
                parentComment = commentRepository.findById(commentRequest.getParentCommentId()).orElseThrow(() -> new RuntimeException("Parent comment not found"));
            }

            Comment comment = Comment.builder()
                    .user(user)
                    .film(film)
                    .content(commentRequest.getContent())
                    .commentTime(LocalDateTime.now())
                    .parentComment(parentComment)
                    .build();
            comment = commentRepository.save(comment);
            filmRepository.increaseComment(film.getFilmId());
            return CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .userId(comment.getUser().getId())
                    .username(comment.getUser().getUsername())
                    .avatarPath(baseUrl + comment.getUser().getAvatarPath())
                    .commentTime(comment.getCommentTime())
                    .content(comment.getContent())
                    .build();
        } else {
            throw new RuntimeException("You can only comment three times.");
        }
    }

    public List<CommentResponse> getCommentsByFilmId(String filmId) {
        // Lấy tất cả comment gốc (parentComment = null)
        List<Comment> parentComments = commentRepository.findByFilm_FilmIdAndParentCommentIsNullOrderByCommentTimeDesc(filmId);

        return parentComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CommentResponse convertToDTO(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .avatarPath(baseUrl + comment.getUser().getAvatarPath())
                .content(comment.getContent())
                .commentTime(comment.getCommentTime())
                .childComments(
                        comment.getChildComments() == null
                                ? List.of()
                                : comment.getChildComments()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
