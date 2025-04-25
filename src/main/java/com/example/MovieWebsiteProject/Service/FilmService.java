package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmRepository filmRepository;

    public Film getFilmById(String filmId) {
        return filmRepository.findById(filmId).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
    }
}
