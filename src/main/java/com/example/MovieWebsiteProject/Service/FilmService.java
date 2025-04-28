package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.dto.response.TopFilmResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmRepository filmRepository;

    public Film getFilmById(String filmId) {
        return filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
    }

    public List<TopFilmResponse> getTopViewFilm(int size) {
        if (size < 1) {
            throw new AppException(ErrorCode.FAILED);
        }

        List<Map<String, Object>> results = filmRepository.getTopViewFilms(size);
        List<TopFilmResponse> response = new ArrayList<>();
        System.out.println("result from repo: " + results.size() + ", " + size);
        for (Map<String, Object> row : results) {
            if (row == null || row.get("belong_to") == null) continue;

            TopFilmResponse.TopFilmResponseBuilder builder = TopFilmResponse.builder()
                    .filmId((String) row.get("film_id"))
                    .belongTo((String) row.get("belong_to"))
                    .numberOfViews((Long) row.get("number_of_views"));

            if ("SYSTEM_FILM".equals(row.get("belong_to"))) {
                builder.title((String) row.get("title"))
                        .backdropPath((String) row.get("backdrop_path"))
                        .posterPath((String) row.get("poster_path"));
                if (row.get("release_date") != null) {
                    builder.releaseDate(((Timestamp) row.get("release_date")).toLocalDateTime());
                }
            } else {
                builder.videoKey((String) row.get("video_key"))
                        .tmdbId((Long) row.get("tmdb_id"));
            }

            response.add(builder.build());
        }
        System.out.println(response.size());
        return response;
    }

    public List<TopFilmResponse> getTopLikeFilm(int size) {
        if (size < 1) {
            throw new AppException(ErrorCode.FAILED);
        }

        List<Map<String, Object>> results = filmRepository.getTopLikeFilms(size);
        List<TopFilmResponse> response = new ArrayList<>();

        for (Map<String, Object> row : results) {
            if (row == null || row.get("belong_to") == null) continue;

            TopFilmResponse.TopFilmResponseBuilder builder = TopFilmResponse.builder()
                    .filmId((String) row.get("film_id"))
                    .belongTo((String) row.get("belong_to"))
                    .numberOfLikes((Long) row.get("number_of_likes"));

            if ("SYSTEM_FILM".equals(row.get("belong_to"))) {
                builder.title((String) row.get("title"))
                        .backdropPath((String) row.get("backdrop_path"))
                        .posterPath((String) row.get("poster_path"));
                if (row.get("release_date") != null) {
                    builder.releaseDate(((Timestamp) row.get("release_date")).toLocalDateTime());
                }
            } else {
                builder.videoKey((String) row.get("video_key"))
                        .tmdbId((Long) row.get("tmdb_id"));
            }

            response.add(builder.build());
        }
        System.out.println(response.size());
        return response;
    }

}
