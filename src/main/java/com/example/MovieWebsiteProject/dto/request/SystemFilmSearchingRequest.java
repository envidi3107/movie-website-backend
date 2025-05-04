package com.example.MovieWebsiteProject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemFilmSearchingRequest {
    String title;
    Boolean adult;
    LocalDate releaseDate;
    String genre;
}
