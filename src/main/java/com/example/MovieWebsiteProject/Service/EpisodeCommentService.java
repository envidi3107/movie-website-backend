package com.example.MovieWebsiteProject.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.MovieWebsiteProject.Dto.response.CommentResponse;
import com.example.MovieWebsiteProject.Entity.Comment.EpisodeComment;
import com.example.MovieWebsiteProject.Entity.Episode;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.EpisodeCommentRepository;
import com.example.MovieWebsiteProject.Repository.EpisodeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeCommentService {
  EpisodeCommentRepository episodeCommentRepository;
  EpisodeRepository episodeRepository;
  AuthenticationService authenticationService;

  @Value("${app.base_url}")
  @NonFinal
  String baseUrl;

  private static final int MAX_COMMENTS_PER_EPISODE = 3;

  public CommentResponse saveComment(int episodeId, String parentCommentId, String content) {
    User user = authentication_service_get();

    int count = episodeCommentRepository.countByUser_IdAndEpisode_Id(user.getId(), episodeId);
    if (count >= MAX_COMMENTS_PER_EPISODE) {
      throw new AppException(ErrorCode.FAILED); // you can create a specific error code if needed
    }

    Episode ep =
        episodeRepository
            .findById(episodeId)
            .orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

    EpisodeComment parent = null;
    if (parentCommentId != null && !parentCommentId.isEmpty()) {
      parent =
          episodeCommentRepository
              .findById(parentCommentId)
              .orElseThrow(() -> new AppException(ErrorCode.FAILED));
    }

    EpisodeComment comment =
        EpisodeComment.builder()
            .user(user)
            .episode(ep)
            .content(content)
            .commentTime(LocalDateTime.now())
            .parentComment(parent)
            .build();

    comment = episodeCommentRepository.save(comment);

    return toDto(comment);
  }

  private User authentication_service_get() {
    return authenticationService.getAuthenticatedUser();
  }

  public List<CommentResponse> getCommentsByEpisodeId(int episodeId) {
    List<EpisodeComment> comments =
        episodeCommentRepository.findByEpisode_IdAndParentCommentIsNullOrderByCommentTimeDesc(
            episodeId);
    return comments.stream().map(this::toDto).collect(Collectors.toList());
  }

  private CommentResponse toDto(EpisodeComment comment) {
    CommentResponse dto =
        CommentResponse.builder()
            .commentId(comment.getCommentId())
            .userId(comment.getUser().getId())
            .username(comment.getUser().getUsername())
            .avatarPath(
                comment.getUser().getAvatarPath() != null
                    ? baseUrl + comment.getUser().getAvatarPath()
                    : null)
            .content(comment.getContent())
            .commentTime(comment.getCommentTime())
            .build();

    if (comment.getChildComments() != null && !comment.getChildComments().isEmpty()) {
      dto.setChildComments(
          comment.getChildComments().stream().map(this::toDto).collect(Collectors.toList()));
    }
    return dto;
  }
}
