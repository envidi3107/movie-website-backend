package com.example.MovieWebsiteProject.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;

    @Column(name = "video_path")
    private String videoPath;

    @ManyToOne
    @JoinColumn(name = "system_film")
    private SystemFilm systemFilm;
}
