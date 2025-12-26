package com.example.MovieWebsiteProject.Entity.Comment;

import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "episode_comment")
public class EpisodeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id")
    private String commentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Size(min = 1, message = "Content is too short!")
    private String content;

    @Column(name = "comment_time")
    private LocalDateTime commentTime;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private EpisodeComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private Set<EpisodeComment> childComments = new HashSet<>();
}
