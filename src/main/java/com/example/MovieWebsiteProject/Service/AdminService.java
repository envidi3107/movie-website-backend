package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Repository.*;
import com.example.MovieWebsiteProject.Dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
import com.example.MovieWebsiteProject.Dto.request.EpisodeRequest;
import com.example.MovieWebsiteProject.Dto.request.FilmRequest;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {
    UserRepository userRepository;
    WatchingRepository watchingRepository;
    CloudinaryService cloudinaryService;
    GenreRepository genreRepository;
    FilmRepository filmRepository;
    UserNotificationService userNotificationService;
    NotificationRepository notificationRepository;
    ReactionRepository reactionRepository;

    @Value("${app.base_url}")
    @NonFinal
    String baseUrl;

    @Value("${app.limit_size}")
    @NonFinal
    int limit_size;

    SimpMessagingTemplate messagingTemplate;

    private String saveVideoToLocal(MultipartFile videoFile) {
        try {
            String folderPath = "upload/videos/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created) {
                    throw new IOException("Could not create folder: " + folderPath);
                }
            }

            String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
            Path filePath = Paths.get(folderPath, fileName);
            Files.copy(videoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + folderPath + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save video locally: " + e.getMessage());
        }
    }


    public Page<UserResponse> getUsers(int page) {
        if (page < 1) {
            throw new AppException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, limit_size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var results = userRepository.findAll(pageRequest);

        return results.map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .avatarPath(user.getAvatarPath() != null ? (baseUrl + user.getAvatarPath()) : null)
                .ipAddress(user.getIpAddress())
                .country(user.getCountry() != null ? user.getCountry().toLowerCase() : null)
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build());
    }

    public List<Map<String, Object>> getMonthlyNewUsers() {
        List<Object[]> results = userRepository.countNewUsersPerMonth();
        List<Map<String, Object>> response = new ArrayList<>();

        results.forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("total_users", row[1]);
            response.add(map);
        });
        return response;
    }

    public List<PopularHourResponse> getMostPopularHours() {
        List<Object[]> rows = watchingRepository.findMostPopularHoursPerDay();
        return rows.stream()
                .map(row -> new PopularHourResponse(
                        ((java.sql.Date) row[0]).toLocalDate(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).longValue()
                )).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopUserLike(int limit) {
        List<Map<String, Object>> results = reactionRepository.getTopUserLike(limit);

        List<Map<String, Object>> responses = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", (String) result.get("user_id"));
            data.put("username", (String) result.get("username"));
            data.put("email", (String) result.get("email"));
            data.put("createAt", ((Timestamp) result.get("created_at")).toLocalDateTime());
            data.put("avatarPath", (String) result.get("avatar_path"));
            data.put("totalLikes", (Long) result.get("total_likes"));
            responses.add(data);
        }

        return responses;
    }

    public String getPublicId(String url) {
        String[] segments = url.split("/");
        String type = segments[segments.length - 2];
        String fileName = segments[segments.length - 1];
        return type + "/" + fileName.substring(0, fileName.lastIndexOf("."));
    }

    // New methods: upload, update, delete system film
    public String uploadSystemFilm(FilmRequest request) {
        try {
            Film film = Film.builder()
                    .adult(request.isAdult())
                    .title(request.getTitle())
                    .overview(request.getOverview())
                    .releaseDate(request.getReleaseDate())
                    .totalDurations(request.getTotalDurations())
                    .isUseSrc(request.isUseSrc())
                    .createdAt(LocalDateTime.now())
                    .build();

            // Handle poster/backdrop
            if (request.isUseSrc()) {
                film.setBackdropPath(request.getBackdropSrc());
                film.setPosterPath(request.getPosterSrc());
            } else {
                if (request.getBackdrop() != null && !request.getBackdrop().isEmpty()) {
                    film.setBackdropPath(cloudinaryService.uploadImage(request.getBackdrop()));
                }
                if (request.getPoster() != null && !request.getPoster().isEmpty()) {
                    film.setPosterPath(cloudinaryService.uploadImage(request.getPoster()));
                }
            }

            // Handle genres
            Set<Genre> genres = new HashSet<>();
            if (request.getGenres() != null) {
                for (String g : request.getGenres()) {
                    Genre genre = genreRepository.findByGenreName(g).orElseGet(() -> {
                        Genre newG = Genre.builder().genreName(g).build();
                        return genreRepository.save(newG);
                    });
                    genres.add(genre);
                }
            }
            film.setGenres(genres);

            // Persist film first to get id for episode foreign key
            Film savedFilm = filmRepository.save(film);

            // Handle episodes
            if (request.getEpisodeRequest() != null && !request.getEpisodeRequest().isEmpty()) {
                Set<Episode> episodes = new HashSet<>();
                int idx = 1;
                for (EpisodeRequest er : request.getEpisodeRequest()) {
                    Episode episode = Episode.builder()
                            .episodeNumber(idx++)
                            .title(er.getName())
                            .film(savedFilm)
                            .build();

                    // video
                    if (request.isUseSrc()) {
                        episode.setVideoPath(er.getVideoUrls());
                    } else {
                        if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                            episode.setVideoPath(cloudinaryService.uploadVideo(er.getVideoFiles()));
                        }
                    }

                    episodes.add(episode);
                }
                savedFilm.setEpisodes(episodes);
                savedFilm = filmRepository.save(savedFilm);
            }

            return savedFilm.getFilmId();
        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_FILE);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED);
        }
    }

    public String updateSystemFilm(String filmId, FilmRequest request) {
        try {
            Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

            film.setAdult(request.isAdult());
            film.setTitle(request.getTitle());
            film.setOverview(request.getOverview());
            film.setReleaseDate(request.getReleaseDate());
            film.setTotalDurations(request.getTotalDurations());
            film.setIsUseSrc(request.isUseSrc());
            film.setUpdatedAt(LocalDateTime.now());

            // Update poster/backdrop
            if (request.isUseSrc()) {
                film.setBackdropPath(request.getBackdropSrc());
                film.setPosterPath(request.getPosterSrc());
            } else {
                if (request.getBackdrop() != null && !request.getBackdrop().isEmpty()) {
                    if (film.getBackdropPath() != null && film.getBackdropPath().contains("http")) {
                        String publicId = getPublicId(film.getBackdropPath());
                        film.setBackdropPath(cloudinaryService.updateImage(publicId, request.getBackdrop()));
                    } else {
                        film.setBackdropPath(cloudinaryService.uploadImage(request.getBackdrop()));
                    }
                }
                if (request.getPoster() != null && !request.getPoster().isEmpty()) {
                    if (film.getPosterPath() != null && film.getPosterPath().contains("http")) {
                        String publicId = getPublicId(film.getPosterPath());
                        film.setPosterPath(cloudinaryService.updateImage(publicId, request.getPoster()));
                    } else {
                        film.setPosterPath(cloudinaryService.uploadImage(request.getPoster()));
                    }
                }
            }

            // Update genres
            Set<Genre> genres = new HashSet<>();
            if (request.getGenres() != null) {
                for (String g : request.getGenres()) {
                    Genre genre = genreRepository.findByGenreName(g).orElseGet(() -> {
                        Genre newG = Genre.builder().genreName(g).build();
                        return genreRepository.save(newG);
                    });
                    genres.add(genre);
                }
            }
            film.setGenres(genres);

            // Replace episodes: remove existing and add new ones
            // Clear existing episodes
            film.getEpisodes().clear();

            if (request.getEpisodeRequest() != null && !request.getEpisodeRequest().isEmpty()) {
                Set<Episode> episodes = new HashSet<>();
                int idx = 1;
                for (EpisodeRequest er : request.getEpisodeRequest()) {
                    Episode episode = Episode.builder()
                            .episodeNumber(idx++)
                            .title(er.getName())
                            .film(film)
                            .build();

                    if (request.isUseSrc()) {
                        episode.setVideoPath(er.getVideoUrls());
                    } else {
                        if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                            // upload or update: if there's an existing video at same index, try update by public id
                            episode.setVideoPath(cloudinaryService.uploadVideo(er.getVideoFiles()));
                        }
                    }

                    episodes.add(episode);
                }
                film.setEpisodes(episodes);
            }

            Film saved = filmRepository.save(film);
            return saved.getFilmId();
        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_FILE);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED);
        }
    }

    public void deleteSystemFilm(String filmId) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        // collect media public ids
        List<String> imagePublicIds = new ArrayList<>();
        if (film.getBackdropPath() != null && film.getBackdropPath().contains("http")) {
            imagePublicIds.add(getPublicId(film.getBackdropPath()));
        }
        if (film.getPosterPath() != null && film.getPosterPath().contains("http")) {
            imagePublicIds.add(getPublicId(film.getPosterPath()));
        }

        // collect video public ids
        List<String> videoPublicIds = new ArrayList<>();
        for (Episode ep : film.getEpisodes()) {
            if (ep.getVideoPath() != null && ep.getVideoPath().contains("http")) {
                videoPublicIds.add(getPublicId(ep.getVideoPath()));
            }
        }

        // delete cloudinary media
        if (!imagePublicIds.isEmpty()) {
            cloudinaryService.deleteImages(imagePublicIds);
        }
        for (String vidPid : videoPublicIds) {
            cloudinaryService.deleteVideo(vidPid);
        }

        // delete film (cascade should remove episodes, reactions, comments, watchings etc.)
        filmRepository.deleteById(filmId);
    }
}
