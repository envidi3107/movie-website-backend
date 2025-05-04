package com.example.MovieWebsiteProject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopFilmResponse {
    private String filmId;
    private String belongTo;
    private String title;
    private String backdropPath, posterPath;
    private LocalDate releaseDate;
    private String tmdbId;
    private long numberOfViews;
    private long numberOfLikes;
}

