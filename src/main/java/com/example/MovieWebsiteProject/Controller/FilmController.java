package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Dto.request.EpisodeRequest;
import com.example.MovieWebsiteProject.Dto.request.FilmRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.EpisodeSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmDetailResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.PageResponse;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.AdminService;
import com.example.MovieWebsiteProject.Service.FilmService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmRepository filmRepository;
    FilmService filmService;
    AdminService adminService;

    @GetMapping("/all")
    public ApiResponse<List<FilmSummaryResponse>> getAllFilms() {
        List<FilmSummaryResponse> results = filmService.getAllFilmsSummary();
        return ApiResponse.<List<FilmSummaryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(results)
                .build();
    }

    @GetMapping("/{filmId}")
    public ApiResponse<FilmDetailResponse> getFilmDetail(@PathVariable("filmId") String filmId) {
        FilmDetailResponse res = filmService.getFilmDetail(filmId);
        return ApiResponse.<FilmDetailResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(res)
                .build();
    }

    @GetMapping("/search")
    public PageResponse<FilmSummaryResponse> searchFilms(@RequestParam(value = "q", required = false) String q,
                                                               @RequestParam(value = "genres", required = false) String genres,
                                                               @RequestParam(value = "adult", required = false) Boolean adult,
                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Set<String> genreSet = null;
        if (genres != null && !genres.isEmpty()) {
            genreSet = Arrays.stream(genres.split(",")).map(String::trim).collect(Collectors.toSet());
        }
        var pageRes = filmService.searchAndFilterFilmsRaw(q, genreSet, adult, page, size);
        List<FilmSummaryResponse> content = pageRes.getContent().stream().map(filmService::mapToSummary).collect(Collectors.toList());
        return new PageResponse<>(pageRes.getNumber() + 1, pageRes.getSize(), pageRes.getTotalElements(), pageRes.getTotalPages(), pageRes.isLast(), content);
    }

    @GetMapping("/episodes/top")
    public ApiResponse<List<EpisodeSummaryResponse>> getTopEpisodes() {
        List<EpisodeSummaryResponse> res = filmService.getTop10EpisodesByViewsLikes();
        return ApiResponse.<List<EpisodeSummaryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(res)
                .build();
    }

    @GetMapping("/episode/{episodeId}")
    public ApiResponse<EpisodeSummaryResponse> getEpisodeDetail(@PathVariable("episodeId") int episodeId) {
        EpisodeSummaryResponse res = filmService.getEpisodeDetail(episodeId);
        return ApiResponse.<EpisodeSummaryResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(res)
                .build();
    }

    @GetMapping("/find")
    public ApiResponse<List<FilmSummaryResponse>> findFilms(@RequestParam("q") String q) {
        var pageRes = filmService.searchAndFilterFilmsRaw(q, null, null, 1, 20);
        List<FilmSummaryResponse> content = pageRes.getContent().stream().map(filmService::mapToSummary).collect(Collectors.toList());
        return ApiResponse.<List<FilmSummaryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(content)
                .build();
    }

    @PostMapping(
            value = "/{filmId}/episodes",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<String> addEpisodesToFilm(
            @PathVariable("filmId") String filmId,
            @RequestPart("episodes") List<EpisodeRequest> episodes
    ) {
        if (episodes == null || episodes.isEmpty()) {
            return ApiResponse.<String>builder()
                    .code(SuccessCode.SUCCESS.getCode())
                    .message("No episodes provided")
                    .results(null)
                    .build();
        }

        String updatedId = adminService.addEpisodesToFilm(filmId, episodes);

        return ApiResponse.<String>builder()
                .code(SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getCode())
                .message(SuccessCode.UPLOAD_FILM_SUCCESSFULLY.getMessage())
                .results(updatedId)
                .build();
    }
}
