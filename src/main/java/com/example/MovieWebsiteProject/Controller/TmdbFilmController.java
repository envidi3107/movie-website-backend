package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Service.TmdbFilmService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.TmdbFilmResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb-films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TmdbFilmController {
    TmdbFilmService tmdbFilmService;

    @PostMapping("/add")
    public ApiResponse<Void> addTmdbFilm(@RequestParam("tmdbId") String tmdbId) {
        tmdbFilmService.addFilm(tmdbId);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<TmdbFilmResponse> getTmdbFilm(@RequestParam("tmdbId") String tmdbId) {
        return ApiResponse.<TmdbFilmResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(tmdbFilmService.getDetail(tmdbId))
                .build();
    }
}
