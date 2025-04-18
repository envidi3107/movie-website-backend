package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Entity.Watching;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.FilmRepository;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.Repository.WatchingRepository;
import com.example.IdentityService.Service.AuthenticationService;
import com.example.IdentityService.Service.WatchingService;
import com.example.IdentityService.dto.request.WatchingRequest;
import com.example.IdentityService.dto.response.ApiResponse;
import com.example.IdentityService.dto.response.FilmHistoryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watching")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingController {
    WatchingRepository watchingRepository;
    WatchingService watchingService;
    FilmRepository filmRepository;
    UserRepository userRepository;
    AuthenticationService authenticationService;

    private String getUserAuthId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-watching-history")
    public ApiResponse<Void> saveFilmWatchingHistory(@RequestBody WatchingRequest request) {
        Film film = filmRepository.findById(request.getFilmId()).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
        User user = userRepository.findById(getUserAuthId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTES));
        Watching watching = Watching.builder()
                .film(film)
                .user(user)
                .timeStamp(request.getTimeStamp())
                .watchHour(request.getWacthHour())
                .build();
        watchingRepository.save(watching);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-watching-history")
    public ApiResponse<List<FilmHistoryResponse>> getFilmWatchingHistory() {
        return ApiResponse.<List<FilmHistoryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(watchingService.getFilmWatchingHistory(authenticationService.getAuthenticatedUser().getId()))
                .build();
    }
}
