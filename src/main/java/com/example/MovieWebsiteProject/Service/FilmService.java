package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.dto.response.TopFilmResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmRepository filmRepository;

    @Value("${app.limit_size}")
    @NonFinal
    int limit_size;

    public Film getFilmById(String filmId) {
        return filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
    }

    public List<TopFilmResponse> getTopViewFilm(int size) {
        if (size < 1 || size > limit_size) {
            throw new AppException(ErrorCode.FAILED);
        }

        List<Map<String, Object>> results = filmRepository.getTopViewFilms(size);
        List<TopFilmResponse> response = new ArrayList<>();

        for (Map<String, Object> row : results) {

            TopFilmResponse.TopFilmResponseBuilder builder = TopFilmResponse.builder()
                    .filmId((String) row.get("film_id"))
                    .belongTo((String) row.get("belong_to"))
                    .numberOfViews((Long) row.get("number_of_views"));

            if ("SYSTEM_FILM".equals(row.get("belong_to"))) {
                builder.title((String) row.get("title"))
                        .backdropPath((String) row.get("backdrop_path"))
                        .posterPath((String) row.get("poster_path"));
                if (row.get("release_date") != null) {
                    builder.releaseDate(((Date) row.get("release_date")).toLocalDate());
                }
            } else {
                System.out.println("tmdb film");
                builder.tmdbId((String) row.get("tmdb_id"));
            }

            response.add(builder.build());
        }
        return response;
    }

    public List<TopFilmResponse> getTopLikeFilm(int size) {
        if (size < 1 || size > limit_size) {
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
                    builder.releaseDate(((Date) row.get("release_date")).toLocalDate());
                }
            } else {
                builder.tmdbId((String) row.get("tmdb_id"));
            }

            response.add(builder.build());
        }
        System.out.println(response.size());
        return response;
    }

    public List<TopFilmResponse> getTopViewLikeSystemFilm(int size) {
        if (size < 1 || size > limit_size) {
            throw new AppException(ErrorCode.FAILED);
        }

        List<Map<String, Object>> results = filmRepository.getTopViewLikeSystemFilm(size);
        List<TopFilmResponse> response = new ArrayList<>();
        for (Map<String, Object> result : results) {
            TopFilmResponse topFilmResponse = TopFilmResponse.builder()
                    .filmId((String) result.get("film_id"))
                    .title((String) result.get("title"))
                    .numberOfLikes((Long) result.get("number_of_likes"))
                    .numberOfViews((Long) result.get("number_of_views"))
                    .backdropPath((String) result.get("backdrop_path"))
                    .posterPath((String) result.get("poster_path"))
                    .releaseDate(((Date) result.get("release_date")).toLocalDate())
                    .build();

            response.add(topFilmResponse);
        }

        return response;
    }
}
