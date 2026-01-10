package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Dto.response.EpisodeDetailResponse;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.Roles;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Repository.EpisodeRepository;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Dto.response.EpisodeSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmDetailResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmSummaryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmRepository filmRepository;
    EpisodeRepository episodeRepository;
    AuthenticationService authenticationService;

    @Value("${app.limit_size}")
    @NonFinal
    int limit_size;

    public Film getFilmById(String filmId) {
        return filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
    }

    // 1) API: get all films including episodes (only show image, title, like, view, releaseDate, adult)
    public List<FilmSummaryResponse> getAllFilmsSummary() {
        List<Film> films = filmRepository.findAll();
        return films.stream().map(this::toFilmSummary).collect(Collectors.toList());
    }

    private FilmSummaryResponse toFilmSummary(Film film) {
        Set<String> genres = film.getGenres() == null ? null : film.getGenres().stream().map(Genre::getGenreName).collect(Collectors.toSet());

        return FilmSummaryResponse.builder()
                .filmId(film.getFilmId())
                .title(film.getTitle())
                .adult(film.isAdult())
                .releaseDate(film.getReleaseDate())
                .backdropPath(film.getBackdropPath())
                .posterPath(film.getPosterPath())
                .type(film.getType())
                .genres(genres)
                .build();
    }

    // 2) API: film detail
    public FilmDetailResponse getFilmDetail(String filmId) {
        Film film = getFilmById(filmId);
        User user = authenticationService.getAuthenticatedUser();

        Timestamp updateTime = user.getRole().equalsIgnoreCase(String.valueOf(Roles.ADMIN)) ? (film.getUpdatedAt() == null ? null : Timestamp.valueOf(film.getUpdatedAt())) : null;

        // episodes: map to EpisodeSummaryResponse
        Set<Episode> eps = film.getEpisodes();
        List<EpisodeDetailResponse> mapped = new ArrayList<>();
        if (eps != null && !eps.isEmpty()) {
            mapped = eps.stream()
                    .sorted(Comparator.comparingInt(Episode::getEpisodeNumber))
                    .map(e -> EpisodeDetailResponse.builder()
                            .id(e.getId())
                            .episodeNumber(e.getEpisodeNumber())
                            .title(e.getTitle())
                            .description(e.getDescription())
                            .posterPath(e.getPosterPath())
                            .backdropPath(e.getBackdropPath())
                            .videoPath(e.getVideoPath())
                            .likeCount(e.getLikeCount())
                            .dislikeCount(e.getDislikeCount())
                            .commentCount(e.getCommentCount())
                            .viewCount(e.getViewCount())
                            .build())
                    .collect(Collectors.toList());
        }

        // genres
        Set<String> gnames = new HashSet<>();
        if (film.getGenres() != null) {
            gnames = film.getGenres().stream().map(Genre::getGenreName).collect(Collectors.toSet());
        }

        return FilmDetailResponse.builder()
                .filmId(film.getFilmId())
                .title(film.getTitle())
                .adult(film.isAdult())
                .overview(film.getOverview())
                .type(film.getType())
                .releaseDate(film.getReleaseDate())
                .backdropPath(film.getBackdropPath())
                .posterPath(film.getPosterPath())
                .numberOfViews(film.getNumberOfViews())
                .numberOfLikes(film.getNumberOfLikes())
                .numberOfDislikes(film.getNumberOfDislikes())
                .numberOfComments(film.getNumberOfComments())
                .createdAt(film.getCreatedAt() == null ? null : Timestamp.valueOf(film.getCreatedAt()))
                .updatedAt(updateTime)
                .episodes(mapped)
                .genres(gnames)
                .build();
    }

    // 3) API: episode detail
    public EpisodeDetailResponse getEpisodeDetail(int episodeId) {
        Episode episode = episodeRepository.findById(episodeId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
        return EpisodeDetailResponse.builder()
                .id(episode.getId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .description(episode.getDescription())
                .posterPath(episode.getPosterPath())
                .backdropPath(episode.getBackdropPath())
                .videoPath(episode.getVideoPath())
                .likeCount(episode.getLikeCount())
                .viewCount(episode.getViewCount())
                .dislikeCount(episode.getDislikeCount())
                .commentCount(episode.getCommentCount())
                .build();
    }

    // 4) API: search + filter films (DB queries + paging)
    public Page<Film> searchAndFilterFilmsRaw(String query, Set<String> genres, Boolean adult, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        String title = (query == null || query.isEmpty()) ? "" : query.toLowerCase();

        if (genres != null && !genres.isEmpty()) {
            List<String> lowerGenres = genres.stream().map(String::toLowerCase).collect(Collectors.toList());
            long genreCount = lowerGenres.size();
            return filmRepository.findByTitleAndGenres(title.isEmpty() ? null : title, lowerGenres, genreCount, adult, pageable);
        } else {
            if (adult == null) {
                return filmRepository.findByTitleContainingIgnoreCase(title, pageable);
            } else {
                return filmRepository.findByTitleContainingIgnoreCaseAndAdult(title, adult, pageable);
            }
        }
    }

    // 5) API: top 10 episodes by (views + likes)
    public List<EpisodeSummaryResponse> getTop10EpisodesByViewsLikes() {
        List<Episode> all = episodeRepository.findAll();
        return all.stream()
                .sorted(Comparator.comparingLong((Episode e) -> (e.getViewCount() + e.getLikeCount())).reversed())
                .limit(10)
                .map(e -> EpisodeSummaryResponse.builder()
                        .id(e.getId())
                        .episodeNumber(e.getEpisodeNumber())
                        .title(e.getTitle())
                        .posterPath(e.getPosterPath())
                        .backdropPath(e.getBackdropPath())
                        .likeCount(e.getLikeCount())
                        .viewCount(e.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }

    public FilmSummaryResponse mapToSummary(Film film) {
        return toFilmSummary(film);
    }
}
