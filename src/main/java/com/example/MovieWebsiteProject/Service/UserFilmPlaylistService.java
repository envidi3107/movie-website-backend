package com.example.MovieWebsiteProject.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MovieWebsiteProject.Dto.response.FilmSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.PlaylistResponse;
import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistService {
    UserFilmPlaylistRepository userFilmPlaylistRepository;
    AuthenticationService authenticationService;
    FilmRepository filmRepository;
    PlaylistRepository playlistRepository;
    FilmService filmService;

    @Transactional
    public void addFilmToUserPlaylist(String playlistId, String filmId, String ownerFilm) {
        User user = authenticationService.getAuthenticatedUser();
        Film film;
        film = filmRepository.findById(filmId).orElseThrow(() -> new RuntimeException("Film not found"));

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found"));

        UserFilmPlaylist userFilmPlaylist = new UserFilmPlaylist(user, film, playlist, LocalDateTime.now());

        userFilmPlaylistRepository.save(userFilmPlaylist);
    }

    public List<PlaylistResponse> getUserPlaylists() {
        User user = authenticationService.getAuthenticatedUser();
        List<UserFilmPlaylist> entries = userFilmPlaylistRepository.findByUser_Id(user.getId());
        // group by playlist
        Map<String, List<UserFilmPlaylist>> grouped = entries.stream().collect(Collectors.groupingBy(e -> e.getPlaylist().getPlaylistId()));
        List<PlaylistResponse> responses = new ArrayList<>();
        for (Map.Entry<String, List<UserFilmPlaylist>> entry : grouped.entrySet()) {
            Playlist p = entry.getValue().get(0).getPlaylist();
            List<FilmSummaryResponse> films = entry.getValue().stream().sorted(Comparator.comparing(e -> e.getAddedTime())).map(e -> filmService.mapToSummary(e.getFilm())).collect(Collectors.toList());
            PlaylistResponse pr = PlaylistResponse.builder().playlistId(p.getPlaylistId()).playlistName(p.getPlaylistName()).createdAt(p.getCreatedAt()).films(films).build();
            responses.add(pr);
        }
        return responses;
    }

    @Transactional
    public void deletePlaylist(String playlistId) {
        User user = authenticationService.getAuthenticatedUser();
        Playlist p = playlistRepository.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!p.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }
        playlistRepository.deleteById(playlistId);
    }
}
