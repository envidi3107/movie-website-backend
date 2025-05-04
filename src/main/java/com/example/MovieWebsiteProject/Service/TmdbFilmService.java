package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.TmdbFilm;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.TmdbFilmRepository;
import com.example.MovieWebsiteProject.dto.response.TmdbFilmResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TmdbFilmService {
    TmdbFilmRepository tmdbFilmRepository;
    private final FilmRepository filmRepository;

    public void addFilm(String tmdbId) {
        TmdbFilm tmdbFilm = TmdbFilm.builder().tmdbId(tmdbId).build();
        Film film = Film.builder()
                .tmdbFilm(tmdbFilm)
                .belongTo("TMDB_FILM")
                .build();
        tmdbFilm.setFilm(film);
        filmRepository.save(film);
    }

    public TmdbFilmResponse getDetail(String tmdbId) {
        Map<String, Object> result = tmdbFilmRepository.getTmdbFilmByTmdbId(tmdbId);
        return TmdbFilmResponse.builder()
                .id((String) result.get("film_id"))
                .tmdbId((String) result.get("tmdb_id"))
                .numberOfViews((Long) result.get("number_of_views"))
                .numberOfLikes((Long) result.get("number_of_likes"))
                .numberOfDislikes((Long) result.get("number_of_dislikes"))
                .build();
    }
}
