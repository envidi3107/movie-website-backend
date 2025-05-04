package com.example.MovieWebsiteProject.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemFilmRequest {
    @NotNull
    private boolean adult;

    @NotNull
    @NotEmpty(message = "Title cannot be empty!")
    private String title;

    @NotNull
    @NotEmpty(message = "Overview cannot be empty!")
    private String overview;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull
    private MultipartFile backdrop;

    @NotNull
    private MultipartFile poster;

    @NotNull
    private MultipartFile video;

    @NotNull
    @NotEmpty(message = "Total of durations cannot be empty!")
    private double totalDurations;

    @NotNull
    @NotEmpty(message = "Genres cannot be empty!")
    private Set<String> genres;
}
