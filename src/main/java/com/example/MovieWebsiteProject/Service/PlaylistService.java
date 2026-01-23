package com.example.MovieWebsiteProject.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.MovieWebsiteProject.Dto.response.PlaylistResponse;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistService {
    PlaylistRepository playlistRepository;
    AuthenticationService authenticationService;
    UserRepository userRepository;

    public PlaylistResponse createUserPlaylist(String playlistName) {
        User user = authenticationService.getAuthenticatedUser();
        if (!playlistRepository.existsByPlaylistNameAndCreatedBy_Id(playlistName, user.getId())) {
            Playlist playlist = Playlist.builder().playlistName(playlistName).createdBy(user).createdAt(LocalDateTime.now()).build();
            playlist = playlistRepository.save(playlist);
            return PlaylistResponse.builder().playlistId(playlist.getPlaylistId()).playlistName(playlist.getPlaylistName()).createdAt(playlist.getCreatedAt()).build();
        } else {
            throw new AppException(ErrorCode.PLAYLIST_ALREADY_EXISTED);
        }
    }

    public List<PlaylistResponse> getUserPlaylist() {
        String userId = authenticationService.getAuthenticatedUser().getId();

        List<PlaylistResponse> responses = new ArrayList<>();
        List<Map<String, Object>> results = playlistRepository.getPlaylistsByUserId(userId);
        for (Map<String, Object> row : results) {
            PlaylistResponse playlistResponse = PlaylistResponse.builder().playlistId((String) row.get("playlist_id")).playlistName((String) row.get("playlist_name")).createdAt(((Timestamp) row.get("created_at")).toLocalDateTime()).build();
            responses.add(playlistResponse);
        }
        return responses;
    }

    public void updatePlaylist(String playlistId, String playlistName) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new AppException(ErrorCode.FAILED));
        User user = userRepository.findById(authenticationService.getAuthenticatedUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));
        playlist.setPlaylistName(playlistName);

        playlistRepository.save(playlist);
    }

    public void deletePlaylist(String playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
