package com.example.MovieWebsiteProject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
  private Long id;
  private String title;
  private String description;
  private String posterUrl;
  private String actionUrl;
}
