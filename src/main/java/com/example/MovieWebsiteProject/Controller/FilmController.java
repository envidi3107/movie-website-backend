package com.example.IdentityService.Controller;

import com.example.IdentityService.Repository.FilmRepository;
import com.example.IdentityService.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmRepository filmRepository;

    @PostMapping("/{filmId}/increase-view")
    public void increaseView(@PathVariable("filmId") String filmId) {
        filmRepository.increaseView(filmId);
    }

    @PostMapping("/{filmId}/increase-like")
    public void increaseLike(@PathVariable("filmId") String filmId) {
        filmRepository.increaseLike(filmId);
    }

    @PostMapping("/{filmId}/decrease-like")
    public void decreaseLike(@PathVariable("filmId") String filmId) {
        filmRepository.decreaseLike(filmId);
    }

    @PostMapping("/{filmId}/increase-dislike")
    public void increaseDislike(@PathVariable("filmId") String filmId) {
        filmRepository.increaseDislike(filmId);
    }

    @PostMapping("/{filmId}/decrease-dislike")
    public void decreaseDislike(@PathVariable("filmId") String filmId) {
        filmRepository.decreaseDislike(filmId);
    }

    @PostMapping("/{filmId}/increase-comment")
    public void increaseComment(@PathVariable("filmId") String filmId) {
        filmRepository.increaseComment(filmId);
    }

    @PostMapping("/{filmId}/decrease-comment")
    public void decreaseComment(@PathVariable("filmId") String filmId) {
        filmRepository.decreaseComment(filmId);
    }
}
