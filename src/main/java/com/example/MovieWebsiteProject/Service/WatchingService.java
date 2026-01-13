package com.example.MovieWebsiteProject.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Entity.Watching;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Enum.FilmType;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.WatchingRepository;
import com.example.MovieWebsiteProject.Dto.response.WatchingHistoryResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingService {
  WatchingRepository watchingRepository;
  FilmRepository filmRepository;
  AuthenticationService authenticationService;

  @Value("${app.limit_size}")
  @NonFinal
  int limit_size;

  private String getUserAuthId() {
    return authenticationService.getAuthenticatedUser().getId();
  }

  public void saveFilmWatchingHistory(String filmId) {
    User user = authenticationService.getAuthenticatedUser();
    Film film =
        filmRepository
            .findById(filmId)
            .orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

    Watching watching =
        Watching.builder()
            .film(film)
            .user(user)
            .watchTime(LocalDateTime.now())
            .watchHour(LocalDateTime.now().getHour())
            .build();
    watchingRepository.save(watching);
  }

  public void saveWatchedDuration(String filmId, long duration) {
    String userId = authenticationService.getAuthenticatedUser().getId();

    Optional<Watching> results =
        watchingRepository.findNewWatchingByUserIdAndFilmId(userId, filmId);

    if (results.isPresent()) {
      Watching watching = results.get();
      watching.setWatchedDuration(duration);
      watchingRepository.save(watching);
    } else {
      throw new AppException(ErrorCode.FAILED);
    }
  }

  public List<WatchingHistoryResponse> getWatchingHistoryByType(String type) {
    User user = authenticationService.getAuthenticatedUser();

    if (type.equalsIgnoreCase("movie")) {
      return watchingRepository.findByUserAndFilmType(user, FilmType.MOVIE)
          .stream()
          .map(WatchingHistoryResponse::new)
          .toList();
    } else {
      throw new AppException(ErrorCode.INVALID_TYPE);
    }
  }
}
