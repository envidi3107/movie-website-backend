package com.example.MovieWebsiteProject.Dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.example.MovieWebsiteProject.Validation.ValidFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeRequest {
  private String title;

  @ValidFile private MultipartFile videoFiles;
  private String videoUrls;
  private String duration;
}
