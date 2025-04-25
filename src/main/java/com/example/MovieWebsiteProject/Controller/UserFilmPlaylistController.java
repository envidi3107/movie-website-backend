package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Service.UserFilmPlaylistService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.UserFilmPlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistController {
    UserFilmPlaylistService userFilmPlaylistService;

    @GetMapping("/{userId}/get-user-playlist/system-film")
    public ApiResponse<List<UserFilmPlaylistResponse>> getUserSystemFilmPlaylist(@PathVariable("userId") String userId) {


        return ApiResponse.<List<UserFilmPlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(userFilmPlaylistService.getUserSystemFilmPlaylist(userId))
                .build();
    }

    @GetMapping("/{userId}/get-user-playlist/tmdb-film")
    public ApiResponse<List<UserFilmPlaylistResponse>> getUserTmdbFilmPlaylist(@PathVariable("userId") String userId) {

        return ApiResponse.<List<UserFilmPlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(userFilmPlaylistService.getUserTmdbFilmPlaylist(userId))
                .build();
    }

}
