package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Entity.SystemFilm;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.*;
import com.example.MovieWebsiteProject.dto.request.SystemFilmRequest;
import com.example.MovieWebsiteProject.dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.dto.response.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Value("${app.base_url}")
    @NonFinal
    String baseUrl;

    @Value("${app.limit_size}")
    @NonFinal
    int limit_size;

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

    public String uploadSystemFilm(SystemFilmRequest request) {
        try {
            String backdropUrl = cloudinaryService.uploadImage(request.getBackdrop());
            String posterUrl = cloudinaryService.uploadImage(request.getPoster());

            String videoUrl;
            try {
                videoUrl = cloudinaryService.uploadVideo(request.getVideo());
            } catch (Exception cloudEx) {
                // Fallback: lưu local
                videoUrl = saveVideoToLocal(request.getVideo());
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
                    .build();

            film.setSystemFilm(systemFilm);

            Set<Genre> genres = request.getGenres().stream()
                    .map(genreName -> genreRepository.findByGenreName(genreName)
                            .orElseGet(() -> genreRepository.save(new Genre(genreName, systemFilm))))
                    .collect(Collectors.toSet());
            systemFilm.setGenres(genres);

            filmRepository.save(film);
            systemFilmRepository.save(systemFilm);

            return SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String updateSystemFilm(String filmId, SystemFilmRequest request) {
        try {
            SystemFilm film = systemFilmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

            String backdropUrl = cloudinaryService.updateImage(getPublicId(film.getBackdropPath()), request.getBackdrop());
            String posterUrl = cloudinaryService.updateImage(getPublicId(film.getPosterPath()), request.getPoster());

            String videoUrl;
            try {
                videoUrl = cloudinaryService.uploadVideo(request.getVideo());
            } catch (Exception cloudEx) {
                videoUrl = saveVideoToLocal(request.getVideo());
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

    public String getPublicId(String url) {
        String[] segments = url.split("/");
        return segments[segments.length - 1].split("\\.")[0];
    }
}
