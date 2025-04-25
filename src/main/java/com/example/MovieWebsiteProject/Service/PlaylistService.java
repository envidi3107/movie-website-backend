package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.dto.response.PlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistService {
    PlaylistRepository playlistRepository;

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

    public void createPlaylist(String playlistName) {
        Optional<Playlist> playlistOptional = playlistRepository.findByPlaylistName(playlistName);

        if (playlistOptional.isEmpty()) {
            playlistRepository.save(new Playlist(playlistName, LocalDateTime.now()));
        } else {
            throw new AppException(ErrorCode.PLAYLIST_ALREADY_EXISTED);
        }
    }

    public void updatePlaylist(String playlistId, String playlistName) {
        Playlist playlist = playlistRepository.findByPlaylistName(playlistId).orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_ALREADY_EXISTED));

        playlist.setPlaylistName(playlistName);
        playlistRepository.save(playlist);
    }

    public void deletePlaylist(String playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
