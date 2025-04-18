package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.ReactionType;
import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.Reaction.Reaction;
import com.example.IdentityService.Entity.Reaction.ReactionID;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Repository.FilmRepository;
import com.example.IdentityService.Repository.ReactionRepository;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.Service.AuthenticationService;
import com.example.IdentityService.Service.FilmService;
import com.example.IdentityService.Service.UserService;
import com.example.IdentityService.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    ReactionRepository reactionRepository;
    AuthenticationService authenticationService;
    UserService userService;
    FilmService filmService;
    FilmRepository filmRepository;

    private String getAuthUserId() {
        return authenticationService.getAuthenticatedUser().getId();
    }

    @PostMapping("/save-reaction/{reactionType}/{filmId}")
    public ApiResponse<Void> saveUserReaction(@PathVariable String reactionType, @PathVariable String filmId) {
        User user = userService.getUser(getAuthUserId());
        Film film = filmService.getFilmById(filmId);
        String type = ReactionType.fromString(reactionType).toUpperCase();
        Reaction reaction = new Reaction(user, film, type);
        if(type.equalsIgnoreCase(ReactionType.LIKE.getType())) {
            filmRepository.increaseLike(film.getFilmId());
        } else{
            filmRepository.increaseDislike(film.getFilmId());
        }
        reactionRepository.save(reaction);

        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-reaction")
    public ApiResponse<List<Map<String, String>>> getUserReaction() {
        List<String[]> results = reactionRepository.getReaction(getAuthUserId());
        List<Map<String, String>> response = new ArrayList<>();

        results.forEach(row -> {
            Map<String, String> data = new HashMap<>();
            data.put("film_id", row[0]);
            data.put("reaction_type", row[1]);
            response.add(data);
        });
        return ApiResponse.<List<Map<String, String>>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(response)
                .build();
    }
}
