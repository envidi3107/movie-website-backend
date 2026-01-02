package com.example.MovieWebsiteProject.Entity.Reaction;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EpisodeReactionID implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "episode_id")
    private int episodeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpisodeReactionID that = (EpisodeReactionID) o;
        return Objects.equals(userId, that.userId) && Objects.equals(episodeId, that.episodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, episodeId);
    }
}

