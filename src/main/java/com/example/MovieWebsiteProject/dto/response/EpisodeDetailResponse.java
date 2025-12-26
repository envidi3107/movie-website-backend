package com.example.MovieWebsiteProject.Dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDetailResponse extends EpisodeSummaryResponse {
    private String description;
    private String videoPath;
    private long dislikeCount;
    private long commentCount;
}
