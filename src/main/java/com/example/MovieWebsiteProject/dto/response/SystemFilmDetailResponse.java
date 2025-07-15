package com.example.MovieWebsiteProject.dto.response;

import com.example.MovieWebsiteProject.Entity.Episode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFilmDetailResponse extends SystemFilmSummaryResponse {
    private long numberOfViews;
    private long numberOfLikes;
    private long numberOfDislikes;
    private long numberOfComments;
    private boolean adult;
    private Set<Episode> episodes;
    private String overview;
    private String belongTo;
    private long watchedDuration;
    private double totalDurations;
    private Boolean isUseSrc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;
}
