package com.example.MovieWebsiteProject.Controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeMetadataRequest {

    @NotBlank
    private String title;

    private String description;

    private String videoUrls;

    @NotBlank
    private String duration;
}

