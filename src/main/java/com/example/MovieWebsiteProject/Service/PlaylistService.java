package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.dto.response.PlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistService {
    PlaylistRepository playlistRepository;
    AuthenticationService authenticationService;
    UserRepository userRepository;

    public List<PlaylistResponse> getAllPlaylist() {
        List<PlaylistResponse> responses = new ArrayList<>();
        List<Playlist> results = playlistRepository.findAll();
        for (Playlist row : results) {
            PlaylistResponse playlistResponse = PlaylistResponse.builder()
                    .playlistId(row.getPlaylistId())
                    .playlistName(row.getPlaylistName())
                    .createAt(row.getCreatedAt())
                    .build();
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
