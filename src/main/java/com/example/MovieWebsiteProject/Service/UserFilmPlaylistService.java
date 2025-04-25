package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Repository.UserFilmPlaylistRepository;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.TmdbFilmResponse;
import com.example.MovieWebsiteProject.dto.response.UserFilmPlaylistResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilmPlaylistService {
    UserFilmPlaylistRepository userFilmPlaylistRepository;

    public List<UserFilmPlaylistResponse> getUserSystemFilmPlaylist(String userId) {
        List<Map<String, Object>> results = userFilmPlaylistRepository.getUserSystemFilmPlaylist(userId);

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
                        .watchedDuration((Long) row.get("watched_duration"))
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

    public List<UserFilmPlaylistResponse> getUserTmdbFilmPlaylist(String userId) {
        List<Map<String, Object>> results = userFilmPlaylistRepository.getUserTmdbFilmPlaylist(userId);
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
                if (tmdbFilm.getId().equals(tmdbFilmId)) {
                    tmdbFilmResponse = tmdbFilm;
                    break;
                }
            }
            if (tmdbFilmResponse == null) {
                tmdbFilmResponse = TmdbFilmResponse.builder()
                        .id(tmdbFilmId)
                        .videoKey((String) row.get("video_key"))
                        .tmdbId((Long) row.get("tmdb_id"))
                        .build();
                playlist.getTmdbFilms().add(tmdbFilmResponse);
            }
        }

        return new ArrayList<>(tmdbPlaylistMap.values());
    }
}
