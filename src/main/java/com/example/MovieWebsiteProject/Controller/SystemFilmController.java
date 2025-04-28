package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Repository.SystemFilmRepository;
import com.example.MovieWebsiteProject.Service.SystemFilmService;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmDetailResponse;
import com.example.MovieWebsiteProject.dto.response.SystemFilmSummaryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemFilmController {
    SystemFilmRepository systemFilmRepository;
    SystemFilmService systemFilmService;

    @GetMapping("/summary-list")
    public ApiResponse<List<SystemFilmSummaryResponse>> getAllSystemFilmSummary(@RequestParam(value = "page", defaultValue = "1") int page) {
        return ApiResponse.<List<SystemFilmSummaryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(systemFilmService.getAllSystemFilmSummary(page - 1))
                .build();
    }

    @GetMapping("/{filmId}/detail")
    public ApiResponse<SystemFilmDetailResponse> getSystemFilmDetail(@PathVariable("filmId") String filmId) {
        return ApiResponse.<SystemFilmDetailResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(systemFilmService.getSystemFilmDetail(filmId))
                .build();
    }

//    @GetMapping("/find")
//    public SystemFilmSummaryResponse findSystemfilmByTitle(@RequestParam("title") String title, @RequestParam("page") int page) {
//
//    }
}
