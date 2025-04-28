package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Comment;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.dto.response.CommentResponse;
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

    @Value("${app.base_url}")
    @NonFinal
    String baseUrl;

    public void saveComment(String filmId, String userId, String content) {
        User user = userService.getUser(userId);
        Film film = filmService.getFilmById(filmId);
        int count = commentRepository.countUserCommentFilm(userId, filmId);
        if (count <= 2) {
            Comment comment = Comment.builder()
                    .user(user)
                    .film(film)
                    .content(content)
                    .commentTime(LocalDateTime.now())
                    .build();
            commentRepository.save(comment);
            filmRepository.increaseComment(film.getFilmId());
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
                .username(comment.getUser().getUsername())
                .avatarPath(baseUrl + comment.getUser().getAvatarPath())
                .content(comment.getContent())
                .commentTime(comment.getCommentTime())
                .childComments(comment.getChildComments()
                        .stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
