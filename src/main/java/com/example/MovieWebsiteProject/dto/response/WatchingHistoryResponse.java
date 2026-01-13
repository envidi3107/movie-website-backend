package com.example.MovieWebsiteProject.Dto.response;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Entity.Watching;
import lombok.Data;

import java.util.stream.Collectors;

@Data
public class WatchingHistoryResponse {

  private FilmSummaryResponse film;
  private String watchTime;
  private long watchedDuration;

  public WatchingHistoryResponse(Watching watching) {
    Film filmEntity = watching.getFilm();
    this.film = FilmSummaryResponse.builder()
            .filmId(filmEntity.getFilmId())
            .title(filmEntity.getTitle())
            .adult(filmEntity.isAdult())
            .type(filmEntity.getType())
            .releaseDate(filmEntity.getReleaseDate())
            .rating(filmEntity.getRating())
            .numberOfViews(filmEntity.getNumberOfViews())
            .backdropPath(filmEntity.getBackdropPath())
            .posterPath(filmEntity.getPosterPath())
            .genres(filmEntity.getGenres().stream()
                    .map(Genre::getGenreName)
                    .collect(Collectors.toSet()))
            .build();
    this.watchTime = watching.getWatchTime().toString();
    this.watchedDuration = watching.getWatchedDuration();
  }
}
