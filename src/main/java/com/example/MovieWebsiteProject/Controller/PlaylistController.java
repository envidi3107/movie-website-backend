package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Service.PlaylistService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.PlaylistResponse;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistController {
    PlaylistRepository playlistRepository;
    PlaylistService playlistService;

    @PostMapping("/create-playlist")
    public ApiResponse<String> createPlaylist(@Nonnull @RequestParam("playlistName") String playlistName) {
        playlistService.createPlaylist(playlistName);
        return ApiResponse.<String>builder()
                .code(SuccessCode.CREATE_PLAYLIST_SUCCESSFULLY.getCode())
                .message(SuccessCode.CREATE_PLAYLIST_SUCCESSFULLY.getMessage())
                .build();
    }

    @GetMapping("/get-all-playlist")
    public ApiResponse<List<PlaylistResponse>> getAllPlaylist() {

        return ApiResponse.<List<PlaylistResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(playlistService.getAllPlaylist())
                .build();
    }

    @PostMapping("/update-playlist/{playlistId}")
    public ApiResponse<Void> updatePlaylist(@PathVariable("playlistId") String playlistId, @Nonnull @RequestParam("playlistName") String playlistName) {
        playlistService.updatePlaylist(playlistId, playlistName);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.UPDATE_PLAYLIST_SUCCESSFULLY.getCode())
                .message(SuccessCode.UPDATE_PLAYLIST_SUCCESSFULLY.getMessage())
                .build();
    }

    @DeleteMapping("/delete-playlist/{playlistId}")
    public ApiResponse<Void> deletePlaylist(@PathVariable("playlistId") String playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.UPDATE_PLAYLIST_SUCCESSFULLY.getCode())
                .message(SuccessCode.UPDATE_PLAYLIST_SUCCESSFULLY.getMessage())
                .build();
    }
}
