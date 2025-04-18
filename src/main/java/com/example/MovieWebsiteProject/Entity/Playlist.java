package com.example.IdentityService.Entity;

import com.example.IdentityService.Entity.Belonging.UserFilmPlaylist;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "playlist_name")
    private String playlistName;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL)
    private Set<UserFilmPlaylist> userFilmPlaylists;
}
