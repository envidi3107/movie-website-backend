package com.example.IdentityService.Entity.Belonging;

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
public class UserFilmPlaylistID implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "film_id")
    private String filmId;

    @Column(name = "playlist_id")
    private String playlistId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserFilmPlaylistID that = (UserFilmPlaylistID) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(filmId, that.filmId)
                && Objects.equals(playlistId, that.playlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, filmId, playlistId);
    }
}
