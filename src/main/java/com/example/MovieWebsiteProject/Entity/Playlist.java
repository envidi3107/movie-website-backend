package com.example.MovieWebsiteProject.Entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;

import lombok.*;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL)
    private Set<UserFilmPlaylist> userFilmPlaylists;
}
