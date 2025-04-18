package com.example.IdentityService.Service;

import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.FilmRepository;
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
