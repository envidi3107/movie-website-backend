package com.example.IdentityService.Entity.Belonging;

import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.Playlist;
import com.example.IdentityService.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_film_playlist")
public class UserFilmPlaylist {
    @EmbeddedId
    private UserFilmPlaylistID id;

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

    public UserFilmPlaylist(User user, Film film, Playlist playlist) {
        this.id = new UserFilmPlaylistID(user.getId(), film.getFilmId(), playlist.getPlaylistId());
        this.user = user;
        this.film = film;
        this.playlist = playlist;
    }
}
