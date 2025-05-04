package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.FilmService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.TopFilmResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmRepository filmRepository;
    FilmService filmService;

    @PostMapping("/{filmId}/increase-view")
    public ApiResponse<Void> increaseView(@PathVariable("filmId") String filmId, @RequestParam("watchedDuration") double duration) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
        if (duration >= (0.5 * film.getTotalDurations())) {
            filmRepository.increaseView(filmId);
            return ApiResponse.<Void>builder()
                    .code(SuccessCode.SUCCESS.getCode())
                    .message(SuccessCode.SUCCESS.getMessage())
                    .build();
        } else {
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.FAILED.getCode())
                    .message(ErrorCode.FAILED.getMessage())
                    .build();
        }
    }

    @GetMapping("/top-view-film")
    public ApiResponse<List<TopFilmResponse>> getTopViewFilm(@RequestParam(value = "size", defaultValue = "1") int size) {

        return ApiResponse.<List<TopFilmResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(filmService.getTopViewFilm(size))
                .build();
    }

    @GetMapping("/top-like-film")
    public ApiResponse<List<TopFilmResponse>> getTopLikeFilm(@RequestParam(value = "size", defaultValue = "1") int size) {

        return ApiResponse.<List<TopFilmResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(filmService.getTopLikeFilm(size))
                .build();
    }

}
