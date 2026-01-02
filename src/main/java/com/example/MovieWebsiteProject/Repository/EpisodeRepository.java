package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {

    @Query(value = "SELECT * FROM episode ORDER BY view_count DESC LIMIT :size", nativeQuery = true)
    List<Episode> findTopByViews(@Param("size") int size);

    @Query(value = "SELECT * FROM episode ORDER BY like_count DESC LIMIT :size", nativeQuery = true)
    List<Episode> findTopByLikes(@Param("size") int size);
}

