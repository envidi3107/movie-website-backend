package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.ReactionService;
import com.example.MovieWebsiteProject.Service.EpisodeReactionService;
import com.example.MovieWebsiteProject.Dto.request.ReactionRequest;
import com.example.MovieWebsiteProject.Dto.request.EpisodeReactionRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    AuthenticationService authenticationService;
    ReactionService reactionService;
    EpisodeReactionService episodeReactionService;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-reaction")
    public ApiResponse<Void> saveReaction(@RequestBody ReactionRequest request) {
        reactionService.saveReaction(getAuthUserId(), request.getFilmId(), request.getReactionType());

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/save-episode-reaction")
    public ApiResponse<Void> saveEpisodeReaction(@RequestBody EpisodeReactionRequest request) {
        episodeReactionService.saveEpisodeReaction(getAuthUserId(), request.getEpisodeId(), request.getReactionType());
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-user-reaction")
    public ApiResponse<List<Map<String, String>>> getUserReaction() {
        return ApiResponse.<List<Map<String, String>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(reactionService.getUserReaction(getAuthUserId()))
                .build();
    }
}
