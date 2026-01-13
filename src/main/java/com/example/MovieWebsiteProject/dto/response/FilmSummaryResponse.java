package com.example.MovieWebsiteProject.Dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.example.MovieWebsiteProject.Enum.FilmType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FilmSummaryResponse {
    private String filmId;
    private String title;
    private boolean adult;
    private FilmType type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private double rating;
    private long numberOfViews;

    private String backdropPath;
    private String posterPath;
    private Set<String> genres;
}