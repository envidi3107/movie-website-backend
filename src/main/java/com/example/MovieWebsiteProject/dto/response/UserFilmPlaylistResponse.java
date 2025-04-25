package com.example.MovieWebsiteProject.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilmPlaylistResponse {
    private String playlistId;
    private String playlistName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    private List<SystemFilmDetailResponse> systemFilms;

    private List<TmdbFilmResponse> tmdbFilms;
}
