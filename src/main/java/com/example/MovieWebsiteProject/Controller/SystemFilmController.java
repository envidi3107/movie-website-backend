package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Repository.SystemFilmRepository;
import com.example.MovieWebsiteProject.Service.SystemFilmService;
import com.example.MovieWebsiteProject.dto.request.SystemFilmSearchingRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.PageResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmSummaryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/system-films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemFilmController {
    SystemFilmRepository systemFilmRepository;
    SystemFilmService systemFilmService;

    @GetMapping("/summary-list")
    public PageResponse<SystemFilmSummaryResponse> getAllSystemFilmSummary(@RequestParam(value = "page", defaultValue = "1") int page) {
        var res = systemFilmService.getAllSystemFilmSummary(page);
        return new PageResponse<>(
                res.getNumber() + 1,
                res.getSize(),
                res.getTotalElements(),
                res.getTotalPages(),
                res.isLast(),
                res.getContent()
        );
    }

    @GetMapping("/{filmId}/detail")
    public ApiResponse<SystemFilmDetailResponse> getSystemFilmDetail(@PathVariable("filmId") String filmId) {
        return ApiResponse.<SystemFilmDetailResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(systemFilmService.getSystemFilmDetail(filmId))
                .build();
    }

    @GetMapping("/search")
    public PageResponse<SystemFilmSummaryResponse> searchFilms(
            @RequestParam(value = "adult", required = false) Boolean adult,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "releaseDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        SystemFilmSearchingRequest request = SystemFilmSearchingRequest.builder()
                .adult(adult)
                .title(title)
                .releaseDate(releaseDate)
                .genre(genre)
                .build();

        PageRequest pageable = PageRequest.of(page - 1, 20);
        Page<SystemFilmSummaryResponse> results = systemFilmService.searchSystemFilms(request, pageable);

        return new PageResponse<>(
                (results.getNumber() + 1),
                results.getSize(),
                results.getTotalElements(),
                results.getTotalPages(),
                results.isLast(),
                results.getContent()
        );
    }
}
