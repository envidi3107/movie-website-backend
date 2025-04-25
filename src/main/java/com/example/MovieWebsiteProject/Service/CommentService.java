package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Comment;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.CommentRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    FilmRepository filmRepository;
    UserService userService;
    FilmService filmService;
    CommentRepository commentRepository;

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
}
