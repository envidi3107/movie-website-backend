package com.example.MovieWebsiteProject.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchingRequest {
    @NotNull
    @NotEmpty(message = "Film Id cannot be empty!")
    private String filmId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @NotEmpty(message = "Watching time Id cannot be empty!")
    private LocalDateTime watchingTime;
}
