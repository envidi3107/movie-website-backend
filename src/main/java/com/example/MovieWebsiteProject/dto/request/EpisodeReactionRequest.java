package com.example.MovieWebsiteProject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeReactionRequest {
  private int episodeId;
  private String reactionType;
}
