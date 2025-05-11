package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.AdminService;
import com.example.MovieWebsiteProject.Service.UserService;
import com.example.MovieWebsiteProject.Service.WatchingService;
import com.example.MovieWebsiteProject.dto.request.SystemFilmRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.PageResponse;
import com.example.MovieWebsiteProject.dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.dto.response.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
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
    UserService userService;
    FilmRepository filmRepository;
    WatchingService watchingService;

    @GetMapping("/get-users")
    public PageResponse<UserResponse> getUsers(@NotNull @RequestParam(value = "page", defaultValue = "1") int page) {
        Page<UserResponse> res = adminService.getUsers(page);
        return new PageResponse<>(
                res.getNumber() + 1,
                res.getSize(),
                res.getTotalElements(),
                res.getTotalPages(),
                res.isLast(),
                res.getContent()
        );
    }

    @GetMapping("/users/registrations/monthly")
    public ApiResponse<List<Map<String, Object>>> getMonthlyNewUsers() {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.getMonthlyNewUsers())
                .build();
    }

    @GetMapping("/popular-hours")
    public ApiResponse<List<PopularHourResponse>> getPopularHours() {
        return ApiResponse.<List<PopularHourResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(adminService.getMostPopularHours())
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

    @PutMapping(value = "/update/system-film/{filmId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @DeleteMapping("/delete")
    ApiResponse<User> deleteUser(@NotNull @RequestParam("userId") String userId) {
        userService.deleteUser(userId);

        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setCode(SuccessCode.DELETED_SUCCESSFULLY.getCode());
        apiResponse.setMessage(SuccessCode.DELETED_SUCCESSFULLY.getMessage());
        return apiResponse;
    }
}
