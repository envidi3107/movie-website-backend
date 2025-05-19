package com.example.MovieWebsiteProject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmWatchingHistoryResponse {
    private String filmId;
    private String belongTo;
    private String title;
    private String backdropPath, posterPath, videoPath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime watchingDate;

    private String tmdbId;
    private long watchedDuration;
    private double totalDurations;
}
