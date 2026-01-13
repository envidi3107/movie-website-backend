package com.example.MovieWebsiteProject.Dto.response;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FilmDetailResponse extends FilmSummaryResponse {
  private long numberOfLikes;
  private long numberOfDislikes;
  private long numberOfComments;
  private List<EpisodeDetailResponse> episodes;
  private String overview;
  private long watchedDuration;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp updatedAt;
}
