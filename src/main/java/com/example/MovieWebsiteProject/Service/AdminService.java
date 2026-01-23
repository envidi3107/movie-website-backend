package com.example.MovieWebsiteProject.Service;

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

import com.example.MovieWebsiteProject.Controller.EpisodeMetadataRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.MovieWebsiteProject.Dto.request.EpisodeRequest;
import com.example.MovieWebsiteProject.Dto.request.FilmRequest;
import com.example.MovieWebsiteProject.Dto.response.PopularHourResponse;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Genre;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Enum.FilmType;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {
    UserRepository userRepository;
    WatchingRepository watchingRepository;
    CloudinaryService cloudinaryService;
    GenreRepository genreRepository;
    FilmRepository filmRepository;
    FilmService filmService;
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

        return results.map(
                user -> UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).dateOfBirth(user.getDateOfBirth()).avatarPath(user.getAvatarPath() != null ? (baseUrl + user.getAvatarPath()) : null).ipAddress(user.getIpAddress()).country(user.getCountry() != null ? user.getCountry().toLowerCase() : null).createdAt(user.getCreatedAt()).role(user.getRole()).build());
    }

    public List<Map<String, Object>> getMonthlyNewUsers() {
        List<Object[]> results = userRepository.countNewUsersPerMonth();
        List<Map<String, Object>> response = new ArrayList<>();

        results.forEach(
                row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", row[0]);
                    map.put("total_users", row[1]);
                    response.add(map);
                });
        return response;
    }

    public List<PopularHourResponse> getMostPopularHours() {
        List<Object[]> rows = watchingRepository.findMostPopularHoursPerDay();
        return rows.stream().map(
                row -> new PopularHourResponse(
                        ((java.sql.Date) row[0]).toLocalDate(), ((Number) row[1]).intValue(), ((Number) row[2]).intValue(), ((Number) row[3]).longValue())).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopUserLike(int limit) {
        List<Map<String, Object>> results = reactionRepository.getTopUserLike(limit);

        List<Map<String, Object>> responses = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", result.get("user_id"));
            data.put("username", result.get("username"));
            data.put("email", result.get("email"));
            data.put("createAt", ((Timestamp) result.get("created_at")).toLocalDateTime());
            data.put("avatarPath", result.get("avatar_path"));
            data.put("totalLikes", result.get("total_likes"));
            responses.add(data);
        }

        return responses;
    }

    public String uploadFilm(FilmRequest request) {
        // create Film entity
        Film film = new Film();
        film.setTitle(request.getTitle());
        film.setAdult(request.isAdult());
        film.setOverview(request.getOverview());
        film.setCreatedAt(LocalDateTime.now());
        film.setType(request.getType());

        if (request.getReleaseDate() != null && !request.getReleaseDate().isEmpty()) {
            film.setReleaseDate(LocalDate.parse(request.getReleaseDate()));
        }


        // genres
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            Set<Genre> genres = Arrays.stream(request.getGenres().split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(
                    s -> genreRepository.findByGenreName(s).orElseGet(
                            () -> {
                                Genre g = new Genre();
                                g.setGenreName(s);
                                return genreRepository.save(g);
                            })).collect(Collectors.toSet());
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

            // film lẻ
            if (request.getType().equals(FilmType.MOVIE)) {
                double duration = timeSolverService.convertTimeStringToSeconds(request.getDuration());
                Episode ep = new Episode();
                ep.setEpisodeNumber(1);
                ep.setTitle(film.getTitle());
                ep.setDescription(film.getOverview());
                ep.setDuration(duration);
                ep.setFilm(film);
                if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
                    String v_url = cloudinaryService.uploadVideo(request.getVideoFile());
                    ep.setVideoPath(v_url);
                } else if (request.getVideoUrl() != null && !request.getVideoUrl().isEmpty()) {
                    ep.setVideoPath(request.getVideoUrl());
                }
                film.getEpisodes().add(ep);
            } else if (request.getType().equals(FilmType.SERIES)) {
                // series: multiple episodes via files or urls
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    addEpisodesToFilm(film, request.getEpisodes());
                } else {
                    // skip if no episodes
                }
            } else {
                throw new AppException(ErrorCode.INVALID_TYPE);
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
        Film film = filmService.getFilmById(filmId);

        if (request.getTitle() != null) film.setTitle(request.getTitle());
        film.setAdult(request.isAdult());
        if (request.getOverview() != null) film.setOverview(request.getOverview());
        film.setUpdatedAt(LocalDateTime.now());
        if (request.getReleaseDate() != null && !request.getReleaseDate().isEmpty()) {
            film.setReleaseDate(LocalDate.parse(request.getReleaseDate()));
        }

        if (request.getGenres() != null) {
            Set<Genre> genres = Arrays.stream(request.getGenres().split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(
                    s -> genreRepository.findByGenreName(s).orElseGet(
                            () -> {
                                Genre g = new Genre();
                                g.setGenreName(s);
                                return genreRepository.save(g);
                            })).collect(Collectors.toSet());
            film.setGenres(genres);
        }

        try {
            if (request.getPosterFile() != null && !request.getPosterFile().isEmpty()) {
                String publicId = cloudinaryService.getPublicId(film.getPosterPath());
                String url = cloudinaryService.updateImage(publicId, request.getPosterFile());
                film.setPosterPath(url);
            } else if (request.getPosterUrl() != null && !request.getPosterUrl().isEmpty()) {
                film.setPosterPath(request.getPosterUrl());
            }

            if (request.getBackdropFile() != null && !request.getBackdropFile().isEmpty()) {
                String publicId = cloudinaryService.getPublicId(film.getBackdropPath());
                String url = cloudinaryService.updateImage(publicId, request.getBackdropFile());
                film.setBackdropPath(url);
            } else if (request.getBackdropUrl() != null && !request.getBackdropUrl().isEmpty()) {
                film.setBackdropPath(request.getBackdropUrl());
            }

            // For updates: handle episodes via request.getEpisodes() (EpisodeRequest) and use
            if (film.getType() == FilmType.MOVIE) {
                // lấy tập đầu tiên từ db
                Optional<Episode> first = film.getEpisodes().stream().findFirst();
                if (first.isPresent()) {
                    Episode ep = first.get();
                    String url;
                    if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
                        String publicId = cloudinaryService.getPublicId(ep.getVideoPath());
                        url = cloudinaryService.updateVideo(publicId, request.getVideoFile());
                    } else {
                        url = request.getVideoUrl();
                    }
                    ep.setTitle(request.getTitle());
                    ep.setDescription(request.getOverview());
                    ep.setDuration(request.getDuration() != null ? timeSolverService.convertTimeStringToSeconds(request.getDuration()) : 0);
                    ep.setVideoPath(url);
                    film.setEpisodes(Set.of(ep));
                } else {
                    Episode ep = new Episode();
                    ep.setEpisodeNumber(1);
                    ep.setTitle(film.getTitle());
                    ep.setDescription(film.getOverview());
                    String url;
                    if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
                        url = cloudinaryService.uploadImage(request.getVideoFile());
                    } else {
                        url = request.getVideoUrl();
                    }
                    ep.setVideoPath(url);
                    film.setEpisodes(Set.of(ep));
                }
            } else {
                // append episodes from request.getEpisodes()
                if (request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
                    addEpisodesToFilm(film, request.getEpisodes());
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

    public void addEpisodesToFilm(Film film, List<EpisodeRequest> episodes) {
        if (episodes == null || episodes.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        if (!film.getType().equals(FilmType.SERIES)) {
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
                    String v_url = cloudinaryService.uploadVideo(er.getVideoFiles());
                    Episode ep = new Episode();
                    ep.setEpisodeNumber(idx);
                    ep.setTitle(
                            er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                    ep.setDescription(er.getDescription());
                    ep.setVideoPath(v_url);
                    ep.setDuration(epDuration);
                    ep.setFilm(film);
                    film.getEpisodes().add(ep);
                    idx++;
                } else if (er.getVideoUrls() != null && !er.getVideoUrls().isEmpty()) {
                    Episode ep = new Episode();
                    ep.setEpisodeNumber(idx);
                    ep.setTitle(
                            er.getTitle() != null && !er.getTitle().isEmpty() ? er.getTitle() : film.getTitle() + " - Ep " + idx);
                    ep.setDescription(er.getDescription());
                    ep.setVideoPath(er.getVideoUrls());
                    ep.setDuration(epDuration);
                    ep.setFilm(film);
                    film.getEpisodes().add(ep);
                    idx++;
                } else {
                    // skip if no video
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.INVALID_FILE);
        }
    }

    public void createFilmWithEpisodes(String filmId, String episodesJson, List<MultipartFile> videoFiles) throws JsonProcessingException {
        Film film = filmService.getFilmById(filmId);

        ObjectMapper objectMapper = new ObjectMapper();

        List<EpisodeMetadataRequest> metadataList =
                objectMapper.readValue(
                        episodesJson,
                        new TypeReference<List<EpisodeMetadataRequest>>() {}
                );

        List<EpisodeRequest> episodes = new ArrayList<>();

        for (int i = 0; i < metadataList.size(); i++) {

            EpisodeMetadataRequest meta = metadataList.get(i);
            EpisodeRequest episode = new EpisodeRequest();

            episode.setTitle(meta.getTitle());
            episode.setDescription(meta.getDescription());
            episode.setDuration(meta.getDuration());

            // ƯU TIÊN FILE nếu tồn tại
            if (videoFiles != null && i < videoFiles.size()) {
                MultipartFile videoFile = videoFiles.get(i);
                if (videoFile != null && !videoFile.isEmpty()) {
                    episode.setVideoFiles(videoFile);
                }
            }

            // Nếu KHÔNG có file → dùng URL
            if (episode.getVideoFiles() == null) {
                episode.setVideoUrls(meta.getVideoUrls());
            }

            episodes.add(episode);
        }

        addEpisodesToFilm(film, episodes);

        filmRepository.save(film);
    }
    public void deleteFilm(String filmId) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        // delete cloudinary assets when possible
        try {
            List<String> imagePublicIds = new ArrayList<>();
            if (film.getPosterPath() != null && film.getPosterPath().contains("res.cloudinary.com")) {
                imagePublicIds.add(cloudinaryService.getPublicId(film.getPosterPath()));
            }
            if (film.getBackdropPath() != null && film.getBackdropPath().contains("res.cloudinary.com")) {
                imagePublicIds.add(cloudinaryService.getPublicId(film.getBackdropPath()));
            }
            if (!imagePublicIds.isEmpty()) cloudinaryService.deleteImages(imagePublicIds);

            for (Episode e : film.getEpisodes()) {
                if (e.getVideoPath() != null && e.getVideoPath().contains("res.cloudinary.com")) {
                    cloudinaryService.deleteVideo(cloudinaryService.getPublicId(e.getVideoPath()));
                }
            }
        } catch (Exception ignored) {
        }

        filmRepository.delete(film);
    }
}
