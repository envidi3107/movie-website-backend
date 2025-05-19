package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.TmdbFilm;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.*;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.TmdbFilmResponse;
import com.example.MovieWebsiteProject.dto.response.UserFilmPlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistService {
    UserFilmPlaylistRepository userFilmPlaylistRepository;
    AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final PlaylistRepository playlistRepository;
    private final TmdbFilmRepository tmdbFilmRepository;

    @Transactional
    public void addFilmToUserPlaylist(String playlistId, String filmId, String ownerFilm) {
        User user = authenticationService.getAuthenticatedUser();
        Film film;
        if (ownerFilm.equals("SYSTEM_FILM")) {
            film = filmRepository.findById(filmId).orElseThrow(() -> new RuntimeException("Film not found"));
        } else {
            TmdbFilm tmdbFilm = tmdbFilmRepository.findByTmdbId(filmId).orElseThrow(() -> new RuntimeException("Tmdb film not found"));
            film = filmRepository.findById(tmdbFilm.getId()).orElseThrow(() -> new RuntimeException("Film not found"));
        }

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found"));

        UserFilmPlaylist userFilmPlaylist = new UserFilmPlaylist(
                user,
                film,
                playlist,
                LocalDateTime.now()
        );

        userFilmPlaylistRepository.save(userFilmPlaylist);
    }

    public List<UserFilmPlaylistResponse> getUserSystemFilmPlaylist() {
        List<Map<String, Object>> results = userFilmPlaylistRepository.getUserSystemFilmPlaylist(authenticationService.getAuthenticatedUser().getId());

        Map<String, UserFilmPlaylistResponse> playlistMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String playlistId = (String) row.get("playlist_id");
            String systemFilmId = (String) row.get("system_film_id");

            // Lấy nếu đã tồn tại có hoặc tạo mới
            UserFilmPlaylistResponse playlist = playlistMap.computeIfAbsent(playlistId, id ->
                    UserFilmPlaylistResponse.builder()
                            .playlistId(playlistId)
                            .playlistName((String) row.get("playlist_name"))
                            .createdAt((Timestamp) row.get("created_at"))
                            .systemFilms(new ArrayList<>())
                            .build()
            );
            SystemFilmDetailResponse film = playlist.getSystemFilms().stream().filter(s -> systemFilmId.equals(s.getSystemFilmId())).findFirst().orElseGet(() -> {
                SystemFilmDetailResponse newFilm = SystemFilmDetailResponse.builder()
                        .systemFilmId(systemFilmId)
                        .title((String) row.get("title"))
                        .backdropPath((String) row.get("backdrop_path"))
                        .posterPath((String) row.get("poster_path"))
                        .videoPath((String) row.get("video_path"))
                        .isUseSrc((Boolean) row.get("is_use_src"))
                        .genres(new HashSet<>())
                        .build();
                playlist.getSystemFilms().add(newFilm);
                return newFilm;
            });

            // Thêm genre nếu chưa có
            film.getGenres().add((String) row.get("genre_name"));
        }

        return new ArrayList<>(playlistMap.values());
    }

    public List<UserFilmPlaylistResponse> getUserTmdbFilmPlaylist() {
        List<Map<String, Object>> results = userFilmPlaylistRepository.getUserTmdbFilmPlaylist(authenticationService.getAuthenticatedUser().getId());
        Map<String, UserFilmPlaylistResponse> tmdbPlaylistMap = new LinkedHashMap<>();

        for (Map<String, Object> row : results) {
            String playlistId = (String) row.get("playlist_id");
            String tmdbFilmId = (String) row.get("tmdb_film_id");

            UserFilmPlaylistResponse playlist = tmdbPlaylistMap.computeIfAbsent(playlistId, id -> UserFilmPlaylistResponse.builder()
                    .playlistId(playlistId)
                    .playlistName((String) row.get("playlist_name"))
                    .createdAt((Timestamp) row.get("created_at"))
                    .tmdbFilms(new ArrayList<>())
                    .build());
            TmdbFilmResponse tmdbFilmResponse = null;
            for (TmdbFilmResponse tmdbFilm : playlist.getTmdbFilms()) {
                if (tmdbFilm.getTmdbId().equals(tmdbFilmId)) {
                    tmdbFilmResponse = tmdbFilm;
                    break;
                }
            }
            if (tmdbFilmResponse == null) {
                tmdbFilmResponse = TmdbFilmResponse.builder()
                        .id(tmdbFilmId)
                        .tmdbId((String) row.get("tmdb_id"))
                        .build();
                playlist.getTmdbFilms().add(tmdbFilmResponse);
            }
        }

        return new ArrayList<>(tmdbPlaylistMap.values());
    }
}
