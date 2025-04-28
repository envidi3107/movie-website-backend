package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.ReactionService;
import com.example.MovieWebsiteProject.dto.request.ReactionRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
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

    @GetMapping("/get-user-reaction")
    public ApiResponse<List<Map<String, String>>> getUserReaction() {
        return ApiResponse.<List<Map<String, String>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(reactionService.getUserReaction(getAuthUserId()))
                .build();
    }
}
