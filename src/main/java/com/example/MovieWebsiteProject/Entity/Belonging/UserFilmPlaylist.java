package com.example.MovieWebsiteProject.Entity.Belonging;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_film_playlist")
public class UserFilmPlaylist {
    @EmbeddedId
    private UserFilmPlaylistId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(name = "added_time")
    private LocalDateTime addedTime;

    public UserFilmPlaylist(User user, Film film, Playlist playlist, LocalDateTime addedTime) {
        this.id = new UserFilmPlaylistId(user.getId(), film.getFilmId(), playlist.getPlaylistId());
        this.user = user;
        this.film = film;
        this.playlist = playlist;
        this.addedTime = addedTime;
    }
}
