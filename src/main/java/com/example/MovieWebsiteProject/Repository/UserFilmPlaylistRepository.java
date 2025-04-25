package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylist;
import com.example.MovieWebsiteProject.Entity.Belonging.UserFilmPlaylistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserFilmPlaylistRepository extends JpaRepository<UserFilmPlaylist, UserFilmPlaylistId> {

    @Query(value = "SELECT ufp.playlist_id, p.playlist_name, p.created_at, sf.system_film_id, film.belong_to, sf.title, sf.backdrop_path, sf.poster_path, sf.video_path, g.genre_name, film.watched_duration\n" +
            "FROM user_film_playlist AS ufp\n" +
            "JOIN system_film AS sf ON ufp.film_id = sf.system_film_id\n" +
            "JOIN film ON sf.system_film_id = film.film_id\n" +
            "JOIN playlist AS p ON ufp.playlist_id = p.playlist_id\n" +
            "JOIN system_film_genres AS sfg ON sf.system_film_id = sfg.system_film_id\n" +
            "JOIN genre AS g ON sfg.genre_id = g.genre_id\n" +
            "WHERE ufp.user_id = :userId", nativeQuery = true)
    List<Map<String, Object>> getUserSystemFilmPlaylist(@Param("userId") String userId);

    @Query(value = "SELECT ufp.playlist_id, p.playlist_name, p.created_at, ufp.film_id as tmdb_film_id, film.belong_to, film.watched_duration, tf.video_key, tf.tmdb_id\n" +
            "FROM user_film_playlist AS ufp\n" +
            "JOIN tmdb_film AS tf ON ufp.film_id = tf.id\n" +
            "JOIN film ON film.film_id = tf.id\n" +
            "JOIN playlist AS p ON ufp.playlist_id = p.playlist_id\n" +
            "WHERE ufp.user_id = :userId", nativeQuery = true)
    List<Map<String, Object>> getUserTmdbFilmPlaylist(@Param("userId") String userId);
}
