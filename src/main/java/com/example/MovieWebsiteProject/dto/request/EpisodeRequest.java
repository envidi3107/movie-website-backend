package com.example.MovieWebsiteProject.dto.request;

import com.example.MovieWebsiteProject.Validation.ValidFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeRequest {
    private String name;

    @ValidFile
    private MultipartFile videoFiles;
    private String videoUrls;
}
