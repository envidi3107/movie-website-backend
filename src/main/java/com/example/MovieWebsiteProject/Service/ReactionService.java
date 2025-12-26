package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Enum.ReactionType;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Reaction.Reaction;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.ReactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionService {
    FilmRepository filmRepository;
    UserService userService;
    FilmService filmService;
    ReactionRepository reactionRepository;

    public void saveReaction(String userId, String filmId, String reactionType) {
        String reactionTypeUpper = reactionType.toUpperCase();
        ReactionType.checkInvalidReaction(reactionTypeUpper);
        User user = userService.getUser(userId);
        Film film = filmService.getFilmById(filmId);

        reactionRepository.getReactionByUserIdAndFilmId(userId, filmId).ifPresentOrElse(record -> {
            if (record.getReactionType().equalsIgnoreCase(reactionTypeUpper)) {
                // undo like or dislike
                reactionRepository.delete(record);
                if (reactionTypeUpper.equals(ReactionType.LIKE.name()) && film.getNumberOfLikes() > 0) {
                    film.setNumberOfLikes(film.getNumberOfLikes() - 1);
                } else if (reactionTypeUpper.equals(ReactionType.DISLIKE.name()) && film.getNumberOfDislikes() > 0) {
                    film.setNumberOfDislikes(film.getNumberOfDislikes() - 1);
                }
            } else {
                // switch like -> dislike
                record.setReactionType(reactionTypeUpper);

                if (reactionTypeUpper.equals(ReactionType.LIKE.name())) {
                    film.setNumberOfLikes(film.getNumberOfLikes() + 1);
                    film.setNumberOfDislikes(film.getNumberOfDislikes() - 1);
                } else {
                    film.setNumberOfLikes(film.getNumberOfLikes() - 1);
                    film.setNumberOfDislikes(film.getNumberOfDislikes() + 1);
                }
            }
        }, () -> {
            Reaction reaction = new Reaction(user, film, reactionTypeUpper, LocalDateTime.now());
            reactionRepository.save(reaction);
            if (reactionTypeUpper.equals(ReactionType.LIKE.name())) {
                film.setNumberOfLikes(film.getNumberOfLikes() + 1);
            } else {
                film.setNumberOfDislikes(film.getNumberOfDislikes() + 1);
            }
        });

        filmRepository.save(film);
    }

    public List<Map<String, String>> getUserReaction(String userId) {
        List<String[]> results = reactionRepository.getUserReaction(userId);
        List<Map<String, String>> response = new ArrayList<>();

        results.forEach(row -> {
            Map<String, String> data = new HashMap<>();
            data.put("film_id", row[0]);
            data.put("reaction_type", row[1]);
            data.put("reaction_time", row[2]);
            response.add(data);
        });

        return response;
    }
}
