package com.example.IdentityService.Service;

import com.example.IdentityService.Repository.WatchingRepository;
import com.example.IdentityService.dto.response.FilmHistoryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingService {
    WatchingRepository watchingRepository;

    public List<FilmHistoryResponse> getFilmWatchingHistory(String userId) {
        List<Map<String, Object>> results = watchingRepository.getFilmWatchingHistory(userId);

        List<FilmHistoryResponse> films = results.stream().map(row -> {
            FilmHistoryResponse film = new FilmHistoryResponse();
            film.setFilmId((String) row.get("film_id"));
            film.setNumberOfViews(((Number) row.get("number_of_views")).intValue());
            film.setNumberOfLikes(((Number) row.get("number_of_likes")).intValue());
            film.setNumberOfDislikes(((Number) row.get("number_of_dislikes")).intValue());
            film.setBelongTo((String) row.get("belong_to"));
            film.setAdult((Boolean) row.get("adult"));
            film.setBackdropPath((String) row.get("backdrop_path"));
            film.setPosterPath((String) row.get("poster_path"));
            film.setVideoPath((String) row.get("video_path"));
            film.setTitle((String) row.get("title"));
            film.setReleaseDate(((Timestamp) row.get("release_date")).toLocalDateTime());
            film.setOverview((String) row.get("overview"));
            film.setWatchTime(((Date) row.get("watch_date")).toLocalDate());
            return film;
        }).collect(Collectors.toList());

        return films;
    }
}
