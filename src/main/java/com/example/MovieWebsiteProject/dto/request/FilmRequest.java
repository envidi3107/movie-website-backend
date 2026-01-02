package com.example.MovieWebsiteProject.Dto.request;

import com.example.MovieWebsiteProject.Validation.ValidFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRequest {
    private String title;
    private boolean adult;
    private String overview;
    private String releaseDate; // ISO date string
    private String genres; // comma separated
    private String type; // MOVIE or SERIES

    // images (either provide file or url)
    @ValidFile
    private MultipartFile posterFile;
    private String posterUrl;

    @ValidFile
    private MultipartFile backdropFile;
    private String backdropUrl;

    // for movie: single video
    @ValidFile
    private MultipartFile videoFile;
    private String videoUrl;

    // for series: multiple episodes can be provided as files or urls (comma separated)
    private List<EpisodeRequest> episodes;
}

