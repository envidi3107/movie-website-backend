package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Enum.ReactionType;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Entity.Reaction.EpisodeReaction;
import com.example.MovieWebsiteProject.Repository.EpisodeRepository;
import com.example.MovieWebsiteProject.Repository.EpisodeReactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeReactionService {
    EpisodeReactionRepository episodeReactionRepository;
    EpisodeRepository episodeRepository;
    UserService userService;

    public void saveEpisodeReaction(String userId, String episodeId, String reactionType) {
        String type = reactionType.toUpperCase();
        ReactionType.checkInvalidReaction(type);

        User user = userService.getUser(userId);
        Episode ep = episodeRepository.findById(episodeId).orElseThrow(() -> new RuntimeException("Episode not found"));

        episodeReactionRepository.getByUserAndEpisode(userId, episodeId).ifPresentOrElse(record -> {
            if (record.getReactionType().equalsIgnoreCase(type)) {
                episodeReactionRepository.delete(record);
                // decrement counts
                if (type.equals(ReactionType.LIKE.name()) && ep.getLikeCount() > 0) ep.setLikeCount(ep.getLikeCount() - 1);
                if (type.equals(ReactionType.DISLIKE.name()) && ep.getDislikeCount() > 0) ep.setDislikeCount(ep.getDislikeCount() - 1);
            } else {
                // switch
                record.setReactionType(type);
                if (type.equals(ReactionType.LIKE.name())) {
                    ep.setLikeCount(ep.getLikeCount() + 1);
                    ep.setDislikeCount(ep.getDislikeCount() - 1);
                } else {
                    ep.setLikeCount(ep.getLikeCount() - 1);
                    ep.setDislikeCount(ep.getDislikeCount() + 1);
                }
            }
        }, () -> {
            EpisodeReaction r = new EpisodeReaction(user, ep, type, LocalDateTime.now());
            episodeReactionRepository.save(r);
            if (type.equals(ReactionType.LIKE.name())) ep.setLikeCount(ep.getLikeCount() + 1);
            else ep.setDislikeCount(ep.getDislikeCount() + 1);
        });

        episodeRepository.save(ep);
    }
}

