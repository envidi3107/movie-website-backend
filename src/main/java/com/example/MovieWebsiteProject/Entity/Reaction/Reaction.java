package com.example.MovieWebsiteProject.Entity.Reaction;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.example.MovieWebsiteProject.Entity.Film;
import com.example.MovieWebsiteProject.Entity.User;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "REACTION")
public class Reaction {
  @EmbeddedId private ReactionID id;

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

  @Column(name = "reaction_time")
  private LocalDateTime reactionTime;

  public Reaction(User user, Film film, String reactionType, LocalDateTime reactionTime) {
    this.id = new ReactionID(user.getId(), film.getFilmId());
    this.user = user;
    this.film = film;
    this.reactionType = reactionType;
    this.reactionTime = reactionTime;
  }
}
