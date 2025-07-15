package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Entity.Notification;
import com.example.MovieWebsiteProject.Entity.SystemFilm;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.*;
import com.example.MovieWebsiteProject.dto.request.SystemFilmRequest;
import com.example.MovieWebsiteProject.dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmSummaryResponse;
import com.example.MovieWebsiteProject.dto.response.UserResponse;
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
    SystemFilmRepository systemFilmRepository;
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

    public String uploadSystemFilm(SystemFilmRequest request) {
        if(!request.isUseSrc() && (request.getBackdrop().isEmpty() || request.getPoster().isEmpty() || request.getVideo().isEmpty()))
            throw new RuntimeException("Media files aren't empty!");

        if(request.isUseSrc() && (request.getBackdropSrc().isEmpty() || request.getPosterSrc().isEmpty() || request.getVideoSrc().isEmpty()))
            throw new RuntimeException("Media url aren't empty!");

        try {
            String backdropUrl, posterUrl;
            Set<String> videoSrcs = new HashSet<>();
            if(request.isUseSrc()) {
                backdropUrl = request.getBackdropSrc();
                posterUrl = request.getPosterSrc();
                videoSrcs = request.getVideoSrc();
            } else {
                backdropUrl = cloudinaryService.uploadImage(request.getBackdrop());
                posterUrl = cloudinaryService.uploadImage(request.getPoster());
                try {
                    videoUrl = cloudinaryService.uploadVideo(request.getVideo());
                } catch (Exception cloudEx) {
                    videoUrl = saveVideoToLocal(request.getVideo());
                }
            }

            Film film = Film.builder()
                    .belongTo("SYSTEM_FILM")
                    .build();

            SystemFilm systemFilm = SystemFilm.builder()
                    .adult(request.isAdult())
                    .title(request.getTitle())
                    .overview(request.getOverview())
                    .releaseDate(request.getReleaseDate())
                    .backdropPath(backdropUrl)
                    .posterPath(posterUrl)
                    .videoPath(videoUrl)
                    .createdAt(LocalDateTime.now())
                    .totalDurations(request.getTotalDurations())
                    .isUseSrc(request.isUseSrc())
                    .build();

            film.setSystemFilm(systemFilm);

            Set<Genre> genres = request.getGenres().stream()
                    .map(genreName -> genreRepository.findByGenreName(genreName)
                            .orElseGet(() -> genreRepository.save(new Genre(genreName, systemFilm))))
                    .collect(Collectors.toSet());
            systemFilm.setGenres(genres);

            filmRepository.save(film);

            systemFilmRepository.save(systemFilm);

            Notification notification = Notification.builder()
                    .title("Phim mới vừa cập bến!")
                    .description(systemFilm.getTitle() + "đã chính thức lên sóng. Xem ngay thôi!!!")
                    .posterUrl(systemFilm.getPosterPath())
                    .actionUrl("/" + systemFilm.getSystemFilmId())
                    .build();

            notification = notificationRepository.save(notification);
            userNotificationService.saveAllUserNotification(notification);

            messagingTemplate.convertAndSend("/topic/new-movie", notification);

            return SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String updateSystemFilm(String filmId, SystemFilmRequest request) {
        try {
            SystemFilm film = systemFilmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

            String backdropUrl = film.getBackdropPath();
            String posterUrl = film.getPosterPath();
            String videoUrl = film.getVideoPath();
            if (!film.getIsUseSrc()) {
                backdropUrl = cloudinaryService.updateImage(getPublicId(film.getBackdropPath()), request.getBackdrop());
                posterUrl = cloudinaryService.updateImage(getPublicId(film.getPosterPath()), request.getPoster());

                try {
                    videoUrl = cloudinaryService.updateVideo(getPublicId(film.getVideoPath()), request.getVideo());
                } catch (Exception cloudEx) {
                    videoUrl = saveVideoToLocal(request.getVideo());
                }
            }

            film.setAdult(request.isAdult());
            film.setTitle(request.getTitle());
            film.setOverview(request.getOverview());
            film.setBackdropPath(backdropUrl);
            film.setPosterPath(posterUrl);
            film.setVideoPath(videoUrl);
            film.setReleaseDate(request.getReleaseDate());
            film.setUpdatedAt(LocalDateTime.now());

            Set<Genre> genres = request.getGenres().stream()
                    .map(genreName -> genreRepository.findByGenreName(genreName)
                            .orElseGet(() -> genreRepository.save(new Genre(genreName, film))))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
            systemFilmRepository.save(film);
            return SuccessCode.UPDATE_FILM_SUCCESSFULLY.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteSystemFilm(String filmId)  {
        SystemFilm film = systemFilmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        filmRepository.deleteById(filmId);

        if (!film.getIsUseSrc()) {
            cloudinaryService.deleteImages(List.of(getPublicId(film.getBackdropPath()), getPublicId(film.getPosterPath())));
            cloudinaryService.deleteVideo(getPublicId(film.getVideoPath()));
        }
    }

    public String getPublicId(String url) {
        String[] segments = url.split("/");
        String type = segments[segments.length - 2];
        String fileName = segments[segments.length - 1];
        return type + "/" + fileName.substring(0, fileName.lastIndexOf("."));
    }
}
