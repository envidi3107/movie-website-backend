package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Dto.request.EpisodeRequest;
import com.example.MovieWebsiteProject.Dto.request.FilmRequest;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Enum.FilmType;
import com.example.MovieWebsiteProject.Repository.*;
import com.example.MovieWebsiteProject.Dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
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
import java.time.LocalDate;
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
    TimeSolverService timeSolverService;
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

    public String uploadFilm(FilmRequest request) {
        // create Film entity
        Film film = new Film();
        film.setTitle(request.getTitle());
        film.setAdult(request.isAdult());
        film.setOverview(request.getOverview());
        film.setCreatedAt(LocalDateTime.now());

        if (request.getReleaseDate() != null && !request.getReleaseDate().isEmpty()) {
            film.setReleaseDate(LocalDate.parse(request.getReleaseDate()));
        }

        // type
        FilmType type = FilmType.MOVIE;
        if (request.getType() != null && request.getType().equalsIgnoreCase("SERIES")) {
            type = FilmType.SERIES;
        }
        film.setType(type);

        // genres
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            Set<Genre> genres = Arrays.stream(request.getGenres().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> genreRepository.findByGenreName(s).orElseGet(() -> {
                        Genre g = new Genre();
                        g.setGenreName(s);
                        return genreRepository.save(g);
                    }))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        // poster/backdrop
        try {
            if (request.getPosterFile() != null && !request.getPosterFile().isEmpty()) {
                String url = cloudinaryService.uploadImage(request.getPosterFile());
                film.setPosterPath(url);
            } else if (request.getPosterUrl() != null && !request.getPosterUrl().isEmpty()) {
                film.setPosterPath(request.getPosterUrl());
            }

            if (request.getBackdropFile() != null && !request.getBackdropFile().isEmpty()) {
                String url = cloudinaryService.uploadImage(request.getBackdropFile());
                film.setBackdropPath(url);
            } else if (request.getBackdropUrl() != null && !request.getBackdropUrl().isEmpty()) {
                film.setBackdropPath(request.getBackdropUrl());
            }
            // process videos/episodes
             // videos
             if (type == FilmType.MOVIE) {
                // single video. Duration can be provided via request.getEpisodes() with single entry
                double duration = 0;
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    EpisodeRequest er = request.getEpisodes().get(0);
                    if (er.getDuration() != null && !er.getDuration().isEmpty()) {
                        duration = timeSolverService.convertTimeStringToSeconds(er.getDuration());
                    }
                    // prefer episode's video if provided there
                    if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                        String vurl = cloudinaryService.uploadVideo(er.getVideoFiles());
                        Episode ep = new Episode();
                        ep.setEpisodeNumber(1);
                        ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle());
                        ep.setVideoPath(vurl);
                        ep.setBackdropPath(film.getBackdropPath());
                        ep.setPosterPath(film.getPosterPath());
                        ep.setDuration(duration);
                        ep.setFilm(film);
                        film.getEpisodes().add(ep);
                    } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                        Episode ep = new Episode();
                        ep.setEpisodeNumber(1);
                        ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle());
                        ep.setVideoPath(er.getVideoUrls());
                        ep.setBackdropPath(film.getBackdropPath());
                        ep.setPosterPath(film.getPosterPath());
                        ep.setDuration(duration);
                        ep.setFilm(film);
                        film.getEpisodes().add(ep);
                    } else {
                        // fallback to top-level videoFile/videoUrl
                        if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
                            String vurl = cloudinaryService.uploadVideo(request.getVideoFile());
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(1);
                            ep.setTitle(film.getTitle());
                            ep.setVideoPath(vurl);
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(duration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                        } else if (request.getVideoUrl() != null && !request.getVideoUrl().isEmpty()) {
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(1);
                            ep.setTitle(film.getTitle());
                            ep.setVideoPath(request.getVideoUrl());
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(duration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                        }
                    }
                } else {
                    // no episode wrapper provided — use top-level videoFile/videoUrl with zero duration
                    double durationFallback = 0;
                    if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
                        String vurl = cloudinaryService.uploadVideo(request.getVideoFile());
                        Episode ep = new Episode();
                        ep.setEpisodeNumber(1);
                        ep.setTitle(film.getTitle());
                        ep.setVideoPath(vurl);
                        ep.setBackdropPath(film.getBackdropPath());
                        ep.setPosterPath(film.getPosterPath());
                        ep.setDuration(durationFallback);
                        ep.setFilm(film);
                        film.getEpisodes().add(ep);
                    } else if (request.getVideoUrl() != null && !request.getVideoUrl().isEmpty()) {
                        Episode ep = new Episode();
                        ep.setEpisodeNumber(1);
                        ep.setTitle(film.getTitle());
                        ep.setVideoPath(request.getVideoUrl());
                        ep.setBackdropPath(film.getBackdropPath());
                        ep.setPosterPath(film.getPosterPath());
                        ep.setDuration(durationFallback);
                        ep.setFilm(film);
                        film.getEpisodes().add(ep);
                    }
                }
             } else {
                 // series: multiple episodes via files or urls
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    int idx = 1;
                    for (EpisodeRequest er : request.getEpisodes()) {
                        // per-episode duration
                        double epDuration = 0;
                        if (er.getDuration() != null && !er.getDuration().isEmpty()) {
                            epDuration = timeSolverService.convertTimeStringToSeconds(er.getDuration());
                        }

                        if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                            String vurl = cloudinaryService.uploadVideo(er.getVideoFiles());
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(idx);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                            ep.setVideoPath(vurl);
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(epDuration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                            idx++;
                        } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(idx);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                            ep.setVideoPath(er.getVideoUrls());
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(epDuration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                            idx++;
                        }
                    }
                }
             }

             Film saved = filmRepository.save(film);
             return saved.getFilmId();

         } catch (Exception e) {
            e.printStackTrace();
            System.out.println("uploadFilm error: " + e.getMessage());
            throw new AppException(ErrorCode.INVALID_FILE);
         }
     }

    public String updateFilm(String filmId, FilmRequest request) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        if (request.getTitle() != null) film.setTitle(request.getTitle());
        film.setAdult(request.isAdult());
        if (request.getOverview() != null) film.setOverview(request.getOverview());
        film.setUpdatedAt(LocalDateTime.now());
        if (request.getReleaseDate() != null && !request.getReleaseDate().isEmpty()) {
            film.setReleaseDate(LocalDate.parse(request.getReleaseDate()));
        }

        if (request.getGenres() != null) {
            Set<Genre> genres = Arrays.stream(request.getGenres().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> genreRepository.findByGenreName(s).orElseGet(() -> {
                        Genre g = new Genre();
                        g.setGenreName(s);
                        return genreRepository.save(g);
                    }))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        try {
            if (request.getPosterFile() != null && !request.getPosterFile().isEmpty()) {
                String url = cloudinaryService.uploadImage(request.getPosterFile());
                film.setPosterPath(url);
            } else if (request.getPosterUrl() != null && !request.getPosterUrl().isEmpty()) {
                film.setPosterPath(request.getPosterUrl());
            }

            if (request.getBackdropFile() != null && !request.getBackdropFile().isEmpty()) {
                String url = cloudinaryService.uploadImage(request.getBackdropFile());
                film.setBackdropPath(url);
            } else if (request.getBackdropUrl() != null && !request.getBackdropUrl().isEmpty()) {
                film.setBackdropPath(request.getBackdropUrl());
            }

            // For updates: handle episodes via request.getEpisodes() (EpisodeRequest) and use timeSolverService
            if (film.getType() == FilmType.MOVIE) {
                Optional<Episode> first = film.getEpisodes().stream().findFirst();
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    EpisodeRequest er = request.getEpisodes().get(0);
                    double duration = 0;
                    if (er.getDuration() != null && !er.getDuration().isEmpty()) {
                        duration = timeSolverService.convertTimeStringToSeconds(er.getDuration());
                    }

                    if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                        String vurl = cloudinaryService.uploadVideo(er.getVideoFiles());
                        if (first.isPresent()) {
                            Episode ep = first.get();
                            ep.setVideoPath(vurl);
                            ep.setDuration(duration);
                            if (er.getTitle() != null && !er.getTitle().isEmpty()) ep.setTitle(er.getTitle());
                        } else {
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(1);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle());
                            ep.setVideoPath(vurl);
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(duration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                        }
                    } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                        if (first.isPresent()) {
                            Episode ep = first.get();
                            ep.setVideoPath(er.getVideoUrls());
                            ep.setDuration(duration);
                            if (er.getTitle() != null && !er.getTitle().isEmpty()) ep.setTitle(er.getTitle());
                        } else {
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(1);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle());
                            ep.setVideoPath(er.getVideoUrls());
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(duration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                        }
                    }
                    // if neither video provided, do nothing
                }
            } else {
                // append episodes from request.getEpisodes()
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    int idx = film.getEpisodes() == null ? 1 : film.getEpisodes().size() + 1;
                    for (EpisodeRequest er : request.getEpisodes()) {
                        double epDuration = 0;
                        if (er.getDuration() != null && !er.getDuration().isEmpty()) {
                            epDuration = timeSolverService.convertTimeStringToSeconds(er.getDuration());
                        }

                        if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                            String vurl = cloudinaryService.uploadVideo(er.getVideoFiles());
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(idx);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                            ep.setVideoPath(vurl);
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(epDuration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                            idx++;
                        } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                            Episode ep = new Episode();
                            ep.setEpisodeNumber(idx);
                            ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                            ep.setVideoPath(er.getVideoUrls());
                            ep.setBackdropPath(film.getBackdropPath());
                            ep.setPosterPath(film.getPosterPath());
                            ep.setDuration(epDuration);
                            ep.setFilm(film);
                            film.getEpisodes().add(ep);
                            idx++;
                        }
                    }
                }
            }

             Film saved = filmRepository.save(film);
             return saved.getFilmId();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("updateFilm error: " + e.getMessage());
            throw new AppException(ErrorCode.INVALID_FILE);
        }
    }

    public String addEpisodesToFilm(String filmId, List<EpisodeRequest> episodes) {
        if (episodes == null || episodes.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        if (film.getType() != FilmType.SERIES) {
            throw new AppException(ErrorCode.FILM_NOT_SERIES);
        }

        int start = (film.getEpisodes() == null) ? 1 : film.getEpisodes().size() + 1;
        try {
            int idx = start;
            for (EpisodeRequest er : episodes) {
                double epDuration = 0;
                if (er.getDuration() != null && !er.getDuration().isEmpty()) {
                    epDuration = timeSolverService.convertTimeStringToSeconds(er.getDuration());
                }

                if (er.getVideoFiles() != null && !er.getVideoFiles().isEmpty()) {
                    String vurl = cloudinaryService.uploadVideo(er.getVideoFiles());
                    Episode ep = new Episode();
                    ep.setEpisodeNumber(idx);
                    ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                    ep.setVideoPath(vurl);
                    ep.setBackdropPath(film.getBackdropPath());
                    ep.setPosterPath(film.getPosterPath());
                    ep.setDuration(epDuration);
                    ep.setFilm(film);
                    film.getEpisodes().add(ep);
                    idx++;
                } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                    Episode ep = new Episode();
                    ep.setEpisodeNumber(idx);
                    ep.setTitle(er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                    ep.setVideoPath(er.getVideoUrls());
                    ep.setBackdropPath(film.getBackdropPath());
                    ep.setPosterPath(film.getPosterPath());
                    ep.setDuration(epDuration);
                    ep.setFilm(film);
                    film.getEpisodes().add(ep);
                    idx++;
                } else {
                    // skip if no video
                }
            }

            Film saved = filmRepository.save(film);
            return saved.getFilmId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.INVALID_FILE);
        }
    }

    public void deleteFilm(String filmId) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        // delete cloudinary assets when possible
        try {
            List<String> imagePublicIds = new ArrayList<>();
            if (film.getPosterPath() != null && film.getPosterPath().contains("res.cloudinary.com")) {
                imagePublicIds.add(getPublicId(film.getPosterPath()));
            }
            if (film.getBackdropPath() != null && film.getBackdropPath().contains("res.cloudinary.com")) {
                imagePublicIds.add(getPublicId(film.getBackdropPath()));
            }
            if (!imagePublicIds.isEmpty()) cloudinaryService.deleteImages(imagePublicIds);

            for (Episode e : film.getEpisodes()) {
                if (e.getVideoPath() != null && e.getVideoPath().contains("res.cloudinary.com")) {
                    cloudinaryService.deleteVideo(getPublicId(e.getVideoPath()));
                }
            }
        } catch (Exception ignored) {
        }

        filmRepository.delete(film);
    }


}
