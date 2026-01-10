package com.example.MovieWebsiteProject.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.Comment.EpisodeComment;

@Repository
public interface EpisodeCommentRepository extends JpaRepository<EpisodeComment, String> {
  List<EpisodeComment> findByEpisode_IdAndParentCommentIsNullOrderByCommentTimeDesc(int episodeId);

  int countByUser_IdAndEpisode_Id(String userId, int episodeId);
}
