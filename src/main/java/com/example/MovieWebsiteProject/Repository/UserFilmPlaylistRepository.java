package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.Belonging.UserFilmPlaylist;
import com.example.IdentityService.Entity.Belonging.UserFilmPlaylistID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFilmPlaylistRepository extends JpaRepository<UserFilmPlaylist, UserFilmPlaylist> {

}
