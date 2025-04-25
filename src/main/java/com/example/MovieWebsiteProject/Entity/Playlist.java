package com.example.MovieWebsiteProject.Entity;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "PLAYLIST")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "playlist_id")
    private String playlistId;

    @Column(name = "playlist_name", unique = true)
    private String playlistName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL)
    private Set<UserFilmPlaylist> userFilmPlaylists;

    public Playlist(String playlistName, LocalDateTime createdAt) {
        this.playlistName = playlistName;
        this.createdAt = createdAt;
    }
}
