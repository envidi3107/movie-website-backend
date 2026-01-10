package com.example.MovieWebsiteProject.Controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.MovieWebsiteProject.Dto.request.FilmRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.PageResponse;
import com.example.MovieWebsiteProject.Dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.AdminService;
import com.example.MovieWebsiteProject.Service.UserService;
import com.example.MovieWebsiteProject.Service.WatchingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
  public PageResponse<UserResponse> getUsers(
      @NotNull @RequestParam(value = "page", defaultValue = "1") int page) {
    Page<UserResponse> res = adminService.getUsers(page);
    return new PageResponse<>(
        res.getNumber() + 1,
        res.getSize(),
        res.getTotalElements(),
        res.getTotalPages(),
        res.isLast(),
        res.getContent());
  }

  @GetMapping("/users/registrations/monthly")
  public ApiResponse<List<Map<String, Object>>> getMonthlyNewUsers() {
    return ApiResponse.<List<Map<String, Object>>>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .results(adminService.getMonthlyNewUsers())
        .build();
  }

  @GetMapping("/top-user-like")
  public ApiResponse<List<Map<String, Object>>> getTopUserLike(@RequestParam("limit") int limit) {
    return ApiResponse.<List<Map<String, Object>>>builder()
        .code(SuccessCode.SUCCESS.getCode())
        .message(SuccessCode.SUCCESS.getMessage())
        .results(adminService.getTopUserLike(limit))
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

  @PostMapping(value = "/upload/film", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<String> uploadFilm(@Valid @ModelAttribute FilmRequest request) {
    String filmId = adminService.uploadFilm(request);
    return ApiResponse.<String>builder()
        .code(SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getCode())
        .message(SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getMessage())
        .results(filmId)
        .build();
  }

  @PutMapping(value = "/update/film/{filmId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<String> updateFilm(
      @PathVariable("filmId") String filmId, @Valid @ModelAttribute FilmRequest request) {
    String updatedId = adminService.updateFilm(filmId, request);
    return ApiResponse.<String>builder()
        .code(SuccessCode.UPDATE_FILM_SUCCESSFULLY.getCode())
        .message(SuccessCode.UPDATE_FILM_SUCCESSFULLY.getMessage())
        .results(updatedId)
        .build();
  }

  @DeleteMapping("/delete/film/{filmId}")
  public ApiResponse<String> deleteFilm(@PathVariable("filmId") String filmId) {
    adminService.deleteFilm(filmId);
    return ApiResponse.<String>builder()
        .code(SuccessCode.DELETE_FILM_SUCCESSFULLY.getCode())
        .message(SuccessCode.DELETE_FILM_SUCCESSFULLY.getMessage())
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
