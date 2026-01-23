package com.example.MovieWebsiteProject.Dto.response;

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
public class EpisodeSummaryResponse {
    private int id;
    private int episodeNumber;
    private String title;
    private long likeCount;
    private long viewCount;
}
