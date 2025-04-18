package com.example.IdentityService.Entity.Reaction;

import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "REACTION")
public class Reaction {
    @EmbeddedId
    private ReactionID id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType;

    public Reaction (User user, Film film, String reactionType) {
        this.id = new ReactionID(user.getId(), film.getFilmId());
        this.user = user;
        this.film = film;
        this.reactionType = reactionType;
    }
}
