package com.example.MovieWebsiteProject.Entity.Reaction;

import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "EPISODE_REACTION")
public class EpisodeReaction {
    @EmbeddedId
    private EpisodeReactionID id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("episodeId")
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType;

    @Column(name = "reaction_time")
    private LocalDateTime reactionTime;

    public EpisodeReaction(User user, Episode episode, String reactionType, LocalDateTime reactionTime) {
        this.id = new EpisodeReactionID(user.getId(), episode.getId());
        this.user = user;
        this.episode = episode;
        this.reactionType = reactionType;
        this.reactionTime = reactionTime;
    }
}

