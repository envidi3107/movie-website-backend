package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Service.UserFilmPlaylistService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.UserFilmPlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistController {
    UserFilmPlaylistService userFilmPlaylistService;

    @GetMapping("/get-user-playlist/system-film")
    public ApiResponse<List<UserFilmPlaylistResponse>> getUserSystemFilmPlaylist() {

        return ApiResponse.<List<UserFilmPlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(userFilmPlaylistService.getUserSystemFilmPlaylist())
                .build();
    }

    @GetMapping("/get-user-playlist/tmdb-film")
    public ApiResponse<List<UserFilmPlaylistResponse>> getUserTmdbFilmPlaylist() {

        return ApiResponse.<List<UserFilmPlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(userFilmPlaylistService.getUserTmdbFilmPlaylist())
                .build();
    }

}
