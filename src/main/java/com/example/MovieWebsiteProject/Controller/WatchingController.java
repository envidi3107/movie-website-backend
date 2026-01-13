package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Dto.response.WatchingHistoryResponse;
import org.springframework.web.bind.annotation.*;

import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.WatchingRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.WatchingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/api/watching")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingController {
  WatchingRepository watchingRepository;
  AuthenticationService authenticationService;
  WatchingService watchingService;
  FilmRepository filmRepository;

  @PostMapping("/save-watching-history")
  public ApiResponse<Void> saveFilmWatchingHistory(@RequestParam("filmId") String filmId) {
    watchingService.saveFilmWatchingHistory(filmId);
    return ApiResponse.<Void>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .build();
  }

  @PostMapping("/save-watched-duration/{filmId}")
  public ApiResponse<Void> saveWatchedDuration(
      @PathVariable("filmId") String filmId, @RequestParam("watchedDuration") long duration) {
    watchingService.saveWatchedDuration(filmId, duration);
    return ApiResponse.<Void>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .build();
  }

  @GetMapping("/history")
  public ApiResponse<List<WatchingHistoryResponse>> getWatchingHistory(@RequestParam("type") String type) {
    List<WatchingHistoryResponse> history = watchingService.getWatchingHistoryByType(type);
    return ApiResponse.<List<WatchingHistoryResponse>>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .results(history)
        .build();
  }
}
