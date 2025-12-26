package com.example.MovieWebsiteProject.Dto.request;

import com.example.MovieWebsiteProject.Validation.ValidFile;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRequest {
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

    private boolean isUseSrc = false;

    @ValidFile
    private MultipartFile backdrop;

    @ValidFile
    private MultipartFile poster;

    private String backdropSrc;
    private String posterSrc;

    private Set<EpisodeRequest> episodeRequest = new HashSet<>();

    @NotNull
    private double totalDurations;

    @NotNull
    @NotEmpty(message = "Genres cannot be empty!")
    private Set<String> genres;
}
