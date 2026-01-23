package com.example.MovieWebsiteProject.Entity;

import com.example.MovieWebsiteProject.Entity.Comment.EpisodeComment;
import com.example.MovieWebsiteProject.Entity.Reaction.EpisodeReaction;
import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "episode")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int episodeNumber;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_path")
    private String videoPath;

    private double duration;
    private long viewCount;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL)
    private List<EpisodeComment> comments;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL)
    private List<EpisodeReaction> reactions;
}
