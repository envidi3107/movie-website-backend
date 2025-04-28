package com.example.MovieWebsiteProject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime releaseDate;
    private String videoKey;
    private long tmdbId;
    private long numberOfViews;
    private long numberOfLikes;
}

