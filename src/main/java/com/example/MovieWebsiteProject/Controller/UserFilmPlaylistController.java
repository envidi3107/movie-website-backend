package com.example.MovieWebsiteProject.Controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.MovieWebsiteProject.Dto.request.PlaylistAdditionRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.PlaylistResponse;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Service.UserFilmPlaylistService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistController {
    UserFilmPlaylistService userFilmPlaylistService;

    @PostMapping("/add-film-to-user-playlist")
    public ApiResponse<Void> addFilmToUserPlaylist(
                                                   @Valid @RequestBody PlaylistAdditionRequest request) {
        userFilmPlaylistService.addFilmToUserPlaylist(
                request.getPlaylistId(), request.getFilmId(), request.getOwnerFilm());
        return ApiResponse.<Void>builder().code(SuccessCode.SUCCESS.getCode()).message(SuccessCode.SUCCESS.getMessage()).build();
    }

    @GetMapping("/playlists")
    public ApiResponse<List<PlaylistResponse>> getUserPlaylists() {
        List<PlaylistResponse> res = userFilmPlaylistService.getUserPlaylists();
        return ApiResponse.<List<PlaylistResponse>>builder().code(SuccessCode.SUCCESS.getCode()).message(SuccessCode.SUCCESS.getMessage()).results(res).build();
    }

    @DeleteMapping("/playlists/{playlistId}")
    public ApiResponse<Void> deletePlaylist(@PathVariable("playlistId") String playlistId) {
        userFilmPlaylistService.deletePlaylist(playlistId);
        return ApiResponse.<Void>builder().code(SuccessCode.SUCCESS.getCode()).message(SuccessCode.SUCCESS.getMessage()).build();
    }
}
