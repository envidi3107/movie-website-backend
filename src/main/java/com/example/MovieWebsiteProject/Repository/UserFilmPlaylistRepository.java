package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFilmPlaylistRepository extends JpaRepository<UserFilmPlaylist, UserFilmPlaylistId> {
    List<UserFilmPlaylist> findByUser_Id(String userId);
}
