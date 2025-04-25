package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.ReactionType;
import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.Reaction.Reaction;
import com.example.MovieWebsiteProject.Entity.Reaction.ReactionID;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.ReactionRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.FilmService;
import com.example.MovieWebsiteProject.Service.ReactionService;
import com.example.MovieWebsiteProject.Service.UserService;
import com.example.MovieWebsiteProject.dto.request.ReactionRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
                .result(reactionService.getUserReaction(getAuthUserId()))
                .build();
    }
}
