package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmRepository filmRepository;

    @PostMapping("/{filmId}/increase-view")
    public ApiResponse<Void> increaseView(@PathVariable("filmId") String filmId, @RequestParam("watchedDuration") long duration) {
        if (duration >= 45) {
            filmRepository.increaseView(filmId);
            return ApiResponse.<Void>builder()
                    .code(SuccessCode.SUCCESS.getCode())
                    .message(SuccessCode.SUCCESS.getMessage())
                    .build();
        } else {
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.FAILED.getCode())
                    .message(ErrorCode.FAILED.getMessage())
                    .build();
        }
    }

//    @GetMapping("/get-all-comments")
//    public getAllComments(@RequestParam("filmId") String filmId) {
//
//    }
}
