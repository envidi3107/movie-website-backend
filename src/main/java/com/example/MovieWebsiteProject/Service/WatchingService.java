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

import java.time.Duration;
import java.time.LocalDate;
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

    public List<FilmWatchingHistoryResponse> getFilmWatchingHistory() {
        List<Map<String, Object>> results = watchingRepository.getFilmWatchingHistory(getUserAuthId());
        List<FilmWatchingHistoryResponse> response = new ArrayList<>();

        results.forEach(row -> {
            FilmWatchingHistoryResponse film;
            if (row.get("belong_to").toString().equals("SYSTEM_FILM")) {
                film = FilmWatchingHistoryResponse.builder()
                        .filmId(row.get("film_id").toString())
                        .title(row.get("title").toString())
                        .belongTo(row.get("belong_to").toString())
                        .videoPath(row.get("video_path").toString())
                        .watchingDate(((LocalDate) row.get("watching_date")))
                        .watchedDuration(((Duration) row.get("watched_duration")))
                        .build();
            } else {
                film = FilmWatchingHistoryResponse.builder()
                        .filmId(row.get("film_id").toString())
                        .videoKey(row.get("video_key").toString())
                        .tmdbId(row.get("tmdb_id").toString())
                        .watchingDate(((LocalDate) row.get("watching_date")))
                        .watchedDuration(((Duration) row.get("watched_duration")))
                        .build();
            }
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
}
