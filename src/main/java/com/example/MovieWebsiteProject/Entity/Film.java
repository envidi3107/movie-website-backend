package com.example.MovieWebsiteProject.Entity;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Reaction.Reaction;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "belong_to")
    private String belongTo;

    @OneToOne(mappedBy = "film", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private TmdbFilm tmdbFilm;

    @OneToOne(mappedBy = "film", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private SystemFilm systemFilm;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private Set<UserFilmPlaylist> userFilmPlaylists = new HashSet<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private Set<Reaction> reactions = new HashSet<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private Set<Watching> watchings = new HashSet<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    public void setSystemFilm(SystemFilm systemFilm) {
        this.systemFilm = systemFilm;
        systemFilm.setFilm(this);
    }
}
