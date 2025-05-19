package com.example.MovieWebsiteProject.Service;


import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Entity.SystemFilm;
import com.example.MovieWebsiteProject.Repository.SystemFilmRepository;
import com.example.MovieWebsiteProject.dto.request.SystemFilmSearchingRequest;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmSummaryResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemFilmService {
    SystemFilmRepository systemFilmRepository;
    AuthenticationService authenticationService;

    @Value("${app.limit_size}")
    @NonFinal
    int limit_size;

    public Page<SystemFilmSummaryResponse> getAllSystemFilmSummary(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit_size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SystemFilm> results = systemFilmRepository.findAll(pageRequest);

        return results.map(row -> SystemFilmSummaryResponse.builder()
                .systemFilmId(row.getSystemFilmId())
                .title(row.getTitle())
                .releaseDate(row.getReleaseDate())
                .backdropPath(row.getBackdropPath())
                .posterPath(row.getPosterPath())
                .genres(row.getGenres().stream().map(Genre::getGenreName).collect(Collectors.toSet()))
                .build());
    }


    public SystemFilmDetailResponse getSystemFilmDetail(String filmId) {
        String userId = authenticationService.getAuthenticatedUser().getId();
        List<Map<String, Object>> results = systemFilmRepository.getSystemFilmDetail(filmId, userId);
        Map<String, Object> firstRow = results.getFirst();
        System.out.println("total_durations: " + firstRow.get("total_durations"));
        SystemFilmDetailResponse film = SystemFilmDetailResponse.builder()
                .systemFilmId(filmId)
                .adult(Boolean.TRUE.equals(firstRow.get("adult")))
                .releaseDate(((Date) firstRow.get("release_date")).toLocalDate())
                .backdropPath((String) firstRow.get("backdrop_path"))
                .posterPath((String) firstRow.get("poster_path"))
                .videoPath((String) firstRow.get("video_path"))
                .title((String) firstRow.get("title"))
                .overview((String) firstRow.get("overview"))
                .createdAt((Timestamp) firstRow.get("created_at"))
                .updatedAt((Timestamp) firstRow.get("updated_at"))

                .numberOfViews((Long) firstRow.get("number_of_views"))
                .numberOfLikes((Long) firstRow.get("number_of_likes"))
                .numberOfDislikes((Long) firstRow.get("number_of_dislikes"))
                .numberOfComments((Long) firstRow.get("number_of_comments"))
                .belongTo((String) firstRow.get("belong_to"))
                .watchedDuration(firstRow.get("watched_duration") == null ? 0 : (Long) firstRow.get("watched_duration"))
                .totalDurations((Double) firstRow.get("total_durations"))
                .isUseSrc((Boolean) firstRow.get("is_use_src"))
                .genres(new HashSet<>())
                .build();
        for (Map<String, Object> row : results) {
            String genreName = (String) row.get("genre_name");
            if (genreName != null) {
                film.getGenres().add(genreName);
            }
        }

        return film;
    }

    public Page<SystemFilmSummaryResponse> searchSystemFilms(SystemFilmSearchingRequest request, PageRequest pageable) {
        Specification<SystemFilm> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }
            if (request.getAdult() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("adult"), request.getAdult()));
            }
            if (request.getReleaseDate() != null) {
                predicate = cb.and(predicate, cb.equal(cb.function("DATE", LocalDate.class, root.get("releaseDate")), request.getReleaseDate()));
            }
            if (request.getGenre() != null && !request.getGenre().isBlank()) {
                Join<Object, Object> genreJoin = root.join("genres");
                predicate = cb.and(predicate, cb.like(cb.lower(genreJoin.get("genreName")), "%" + request.getGenre().toLowerCase() + "%"));
            }

            return predicate;
        };

        var films = systemFilmRepository.findAll(spec, pageable);
        return films.map(f -> SystemFilmSummaryResponse.builder()
                .systemFilmId(f.getSystemFilmId())
                .title(f.getTitle())
                .releaseDate(f.getReleaseDate())
                .posterPath(f.getPosterPath())
                .adult(Boolean.TRUE.equals(f.isAdult()))
                .genres(f.getGenres().stream().map(Genre::getGenreName).collect(Collectors.toSet()))
                .build());
    }
}
