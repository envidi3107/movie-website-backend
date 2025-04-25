package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    Optional<Playlist> findByPlaylistName(String playlistName);
}
