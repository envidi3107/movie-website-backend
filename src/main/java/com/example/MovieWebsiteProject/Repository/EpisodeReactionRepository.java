package com.example.MovieWebsiteProject.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.Reaction.EpisodeReaction;
import com.example.MovieWebsiteProject.Entity.Reaction.EpisodeReactionID;

@Repository
public interface EpisodeReactionRepository
    extends JpaRepository<EpisodeReaction, EpisodeReactionID> {
  @Query(
      value = "SELECT * FROM episode_reaction WHERE user_id = :userId AND episode_id = :episodeId",
      nativeQuery = true)
  Optional<EpisodeReaction> getByUserAndEpisode(
      @Param("userId") String userId, @Param("episodeId") int episodeId);
}
