package com.example.IdentityService.dto.response;

import com.example.IdentityService.Entity.SystemFilm;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmHistoryResponse {
    private String filmId;
    private long numberOfViews, numberOfLikes, numberOfDislikes;
    private String belongTo;
    private boolean adult;
    private String backdropPath, posterPath, videoPath, title, overview;
    private LocalDateTime releaseDate;
    private LocalDate watchTime;
}
