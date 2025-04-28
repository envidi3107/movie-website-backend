package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.GenreRepository;
import com.example.MovieWebsiteProject.Repository.SystemFilmRepository;
import com.example.MovieWebsiteProject.Service.AdminService;
import com.example.MovieWebsiteProject.Service.CloudinaryService;
import com.example.MovieWebsiteProject.dto.request.SystemFilmRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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

//    @GetMapping("/get-users")
//    public ApiResponse<List<UserResponse>> getUsers(@NotNull @RequestParam("page") int page) {
//        return ApiResponse.<List<UserResponse>>builder()
//                .results(adminService.getUsers(int page))
//                .build();
//    }

    @GetMapping("/users/registrations/monthly")
    public ApiResponse<List<Map<String, Object>>> getMonthlyNewUsers() {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.getMonthlyNewUsers())
                .build();
    }

    @GetMapping("/users/watching/hourly")
    public ApiResponse<List<Map<String, Object>>> getHourlyWatchingUsers(@RequestParam("watchDate") String watchDate) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.getUsersWatchingPerHour(watchDate))
                .build();
    }

    @PostMapping(value = "/upload/system-film", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadSystemFilm(@Valid @ModelAttribute SystemFilmRequest request) {
        return ApiResponse.<String>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.uploadSystemFilm(request))
                .build();
    }

    @PatchMapping(value = "/update/system-film/{filmId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateSystemFilm(@PathVariable("filmId") String filmId, @Valid @ModelAttribute SystemFilmRequest request) {
        return ApiResponse.<String>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.updateSystemFilm(filmId, request))
                .build();
    }

    @DeleteMapping("/delete/system-film/{filmId}")
    public ApiResponse<String> deleteSystemFilm(@PathVariable("filmId") String filmId) {
        filmRepository.deleteById(filmId);
        return ApiResponse.<String>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(SuccessCode.DELETE_FILM_SUCCESSFULLY.getMessage())
                .build();
    }
}
