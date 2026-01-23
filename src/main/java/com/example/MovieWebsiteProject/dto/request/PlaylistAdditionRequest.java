package com.example.MovieWebsiteProject.Dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistAdditionRequest {
    @NotNull
    @NotEmpty(message = "Playlist Id cannot be empty!")
    private String playlistId;

    @NotNull
    @NotEmpty(message = "Film Id cannot be empty!")
    private String filmId;

    @NotNull
    @NotEmpty(message = "Owner film cannot be empty!")
    private String ownerFilm;
}
