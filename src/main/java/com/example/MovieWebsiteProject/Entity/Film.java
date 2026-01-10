package com.example.MovieWebsiteProject.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Comment.Comment;
import com.example.MovieWebsiteProject.Entity.Reaction.Reaction;
import com.example.MovieWebsiteProject.Enum.FilmType;

import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "film_id")
  private String filmId;

  @Column(name = "number_of_views")
  private long numberOfViews;

  @Column(name = "number_of_likes")
  private long numberOfLikes;

  @Column(name = "number_of_dislikes")
  private long numberOfDislikes;

  @Column(name = "number_of_comments")
  private long numberOfComments;

  private double rating;

  private boolean adult;

  private String title;

  @Lob
  @Column(name = "overview", columnDefinition = "TEXT")
  private String overview;

  @Column(name = "release_date")
  private LocalDate releaseDate;

  @Column(name = "backdrop_path")
  private String backdropPath;

  @Column(name = "poster_path")
  private String posterPath;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private FilmType type;

  @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
  private Set<Episode> episodes = new HashSet<>();

  @ManyToMany private Set<Genre> genres = new HashSet<>();

  @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
  private Set<UserFilmPlaylist> userFilmPlaylists = new HashSet<>();

  @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
  private Set<Reaction> reactions = new HashSet<>();

  @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
  private Set<Watching> watchings = new HashSet<>();

  @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
  private Set<Comment> comments = new HashSet<>();
}
