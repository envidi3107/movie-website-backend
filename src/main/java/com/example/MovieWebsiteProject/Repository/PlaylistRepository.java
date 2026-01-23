package com.example.MovieWebsiteProject.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    @Query(value = "SELECT * FROM playlist WHERE created_by = :userId", nativeQuery = true)
    List<Map<String, Object>> getPlaylistsByUserId(@Param("userId") String userId);

    boolean existsByPlaylistNameAndCreatedBy_Id(
                                                @Param("playlistName") String playlistName, @Param("userId") String userId);

    void deleteByPlaylistIdAndCreatedBy_Id(
                                           @Param("playlistName") String playlistName, @Param("userId") String userId);
}
