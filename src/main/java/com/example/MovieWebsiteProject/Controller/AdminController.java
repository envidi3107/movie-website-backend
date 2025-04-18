package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.Genre;
import com.example.IdentityService.Entity.SystemFilm;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.FilmRepository;
import com.example.IdentityService.Repository.GenreRepository;
import com.example.IdentityService.Repository.SystemFilmRepository;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.Service.AdminService;
import com.example.IdentityService.Service.CloudinaryService;
import com.example.IdentityService.Service.UserService;
import com.example.IdentityService.dto.request.SystemFilmUploadRequest;
import com.example.IdentityService.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;
    SystemFilmRepository systemFilmRepository;
    CloudinaryService cloudinaryService;
    GenreRepository genreRepository;
    FilmRepository filmRepository;

    @GetMapping("/get-users")
    public ApiResponse<List<User>> getUsers() {
        return ApiResponse.<List<User>>builder()
                .result(adminService.getUsers())
                .build();
    }

    @GetMapping("/users/registrations/monthly")
    public ApiResponse<List<Map<String, Object>>> getMonthlyNewUsers() {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(adminService.getMonthlyNewUsers())
                .build();
    }

    @GetMapping("/users/watching/hourly")
    public ApiResponse<List<Map<String, Object>>> getHourlyWatchingUsers(@RequestParam String dateTime) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(adminService.getUsersWatchingPerHour(dateTime))
                .build();
    }

    @PostMapping(value = "/upload/system-film", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadSystemFilm(@ModelAttribute SystemFilmUploadRequest request) {
        try {
//            String backdropUrl = cloudinaryService.uploadImage(request.getBackdrop());
//            String posterUrl = cloudinaryService.uploadImage(request.getPoster());
//            String videoUrl = cloudinaryService.uploadVideo(request.getVideo());

            Set<Genre> genres = request.getGenres().stream()
                    .map(genreName -> genreRepository.findByName(genreName)
                            .orElseGet(() -> genreRepository.save(new Genre(genreName))))
                    .collect(Collectors.toSet());

            Film film = Film.builder()
                    .belongTo("SYSTEM_FILM")
                    .build();

            LocalDateTime createdAt = LocalDateTime.now();
            SystemFilm systemFilm = SystemFilm.builder()
                    .film(film)
                    .systemFilmId(film.getFilmId())
                    .adult(request.isAdult())
                    .title(request.getTitle())
                    .overview(request.getOverview())
                    .releaseDate(request.getReleaseDate())
//                    .backdropPath(backdropUrl)
//                    .posterPath(posterUrl)
//                    .videoPath(videoUrl)
                    .createdAt(createdAt)
                    .build();
            System.out.println("systemfilm id: " + systemFilm.getSystemFilmId());
            System.out.println("film id: " + film.getFilmId());
            systemFilmRepository.save(systemFilm);
            systemFilm.setGenres(genres);
            systemFilmRepository.save(systemFilm);


            return ApiResponse.<String>builder()
                    .code(SuccessCode.SUCCESS.getCode())
                    .message(SuccessCode.SUCCESS.getMessage())
                    .result(SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.INVALID_FILE.getCode())
                    .message(ErrorCode.INVALID_FILE.getMessage())
                    .result(e.getMessage())
                    .build();
        }
    }

}
