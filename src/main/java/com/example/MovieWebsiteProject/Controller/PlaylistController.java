package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.PlaylistService;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.PlaylistResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistController {
    PlaylistService playlistService;
    PlaylistRepository playlistRepository;
    AuthenticationService authenticationService;

    @GetMapping("/get-user-playlist")
    public ApiResponse<List<PlaylistResponse>> getUserPlaylist() {

        return ApiResponse.<List<PlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(playlistService.getUserPlaylist())
                .build();
    }

    @PostMapping("/create-playlist")
    public ApiResponse<PlaylistResponse> createPlaylist(@NotNull @NotEmpty @RequestParam("playlistName") String playlistName) {
        ;
        return ApiResponse.<PlaylistResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(playlistService.createUserPlaylist(playlistName))
                .build();
    }

    @Transactional
    @DeleteMapping("/delete-user-playlist")
    public ApiResponse<Void> deletePlaylist(@NotNull @NotEmpty @RequestParam("playlistId") String playlistId) {
        playlistRepository.deleteByPlaylistIdAndCreatedBy_Id(playlistId, authenticationService.getAuthenticatedUser().getId());
        return ApiResponse.<Void>builder()
                .code(SuccessCode.DELETE_PLAYLIST_SUCCESSFULLY.getCode())
                .message(SuccessCode.DELETE_PLAYLIST_SUCCESSFULLY.getMessage())
                .build();
    }
}
