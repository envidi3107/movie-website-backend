package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Entity.Watching;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Repository.WatchingRepository;
import com.example.MovieWebsiteProject.dto.response.FilmWatchingHistoryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingService {
    WatchingRepository watchingRepository;
    FilmRepository filmRepository;
    UserRepository userRepository;
    AuthenticationService authenticationService;

    private String getUserAuthId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    public List<FilmWatchingHistoryResponse> getSystemFilmWatchingHistory() {
        List<Map<String, Object>> systemFilmResults = watchingRepository.getSystemFilmWatchingHistory(getUserAuthId());
        List<Map<String, Object>> tmdbFilmResults = watchingRepository.getTmdbFilmWatchingHistory(getUserAuthId());
        List<FilmWatchingHistoryResponse> response = new ArrayList<>();

        systemFilmResults.forEach(row -> {
            FilmWatchingHistoryResponse film = FilmWatchingHistoryResponse.builder()
                    .filmId(row.get("film_id").toString())
                    .title(row.get("title").toString())
                    .backdropPath(row.get("backdrop_path").toString())
                    .posterPath(row.get("poster_path").toString())
                    .videoPath(row.get("video_path").toString())
                    .watchingDate(((Date) row.get("watching_date")).toLocalDate())
                    .watchedDuration(((Long) row.get("watched_duration")))
                    .build();
            response.add(film);
        });

        tmdbFilmResults.forEach(row -> {
            FilmWatchingHistoryResponse film = FilmWatchingHistoryResponse.builder()
                    .filmId(row.get("film_id").toString())
                    .watchingDate(((Date) row.get("watching_date")).toLocalDate())
                    .watchedDuration(((Long) row.get("watched_duration")))
                    .build();
            response.add(film);
        });
        return response;
    }

    public List<FilmWatchingHistoryResponse> getTmdbFilmWatchingHistory() {
        List<Map<String, Object>> tmdbFilmResults = watchingRepository.getTmdbFilmWatchingHistory(getUserAuthId());
        List<FilmWatchingHistoryResponse> response = new ArrayList<>();

        tmdbFilmResults.forEach(row -> {
            FilmWatchingHistoryResponse film = FilmWatchingHistoryResponse.builder()
                    .filmId(row.get("film_id").toString())
                    .watchingDate(((Date) row.get("watching_date")).toLocalDate())
                    .watchedDuration(((Long) row.get("watched_duration")))
                    .build();
            response.add(film);
        });
        return response;
    }

    public void saveFilmWatchingHistory(String filmId) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
        User user = authenticationService.getAuthenticatedUser();
        Watching watching = Watching.builder()
                .film(film)
                .user(user)
                .watchTime(LocalDateTime.now())
                .watchHour(LocalDateTime.now().getHour())
                .build();
        watchingRepository.save(watching);
    }

    public void saveWacthedDuration(String filmId, long duration) {
        String userId = authenticationService.getAuthenticatedUser().getId();

        List<Watching> results = watchingRepository.findByUser_IdAndFilm_FilmId(userId, filmId);

        if (!results.isEmpty()) {
            Watching watching = results.getLast();
            watching.setWatchedDuration(duration);
            watchingRepository.save(watching);
        } else {
            throw new AppException(ErrorCode.FAILED);
        }
    }
}
