package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.EpisodeSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmDetailResponse;
import com.example.MovieWebsiteProject.Dto.response.FilmSummaryResponse;
import com.example.MovieWebsiteProject.Dto.response.PageResponse;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Service.FilmService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmRepository filmRepository;
    FilmService filmService;

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
    public ApiResponse<EpisodeSummaryResponse> getEpisodeDetail(@PathVariable("episodeId") String episodeId) {
        EpisodeSummaryResponse res = filmService.getEpisodeDetail(episodeId);
        return ApiResponse.<EpisodeSummaryResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(res)
                .build();
    }
}
