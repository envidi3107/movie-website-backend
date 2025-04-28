package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Watching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WatchingRepository extends JpaRepository<Watching, String> {

    @Query(value = "SELECT COUNT(DISTINCT user_id) AS total_users, watching_hour, date(watching_time) as watching_date\n" +
            "FROM watching\n" +
            "GROUP BY watching_hour, watching_time\n" +
            "HAVING watching_date = :watchingDate\n" +
            "ORDER BY watching_hour", nativeQuery = true)
    List<Object[]> countUsersWatchingPerHour(@Param("watchingDate") String watchingDate);

    @Query(value = "SELECT DISTINCT\n" +
            "    w.film_id, \n" +
            "    sf.title, \n" +
            "    sf.backdrop_path,\n" +
            "    sf.poster_path,\n" +
            "    sf.video_path, \n" +
            "    DATE(w.watching_time) AS watching_date,\n" +
            "    w.watched_duration\n" +
            "FROM watching AS w\n" +
            "JOIN system_film AS sf ON w.film_id = sf.system_film_id\n" +
            "WHERE w.user_id = 'u1'\n" +
            "ORDER BY DATE(w.watching_time) DESC\n" +
            "LIMIT 20;", nativeQuery = true)
    List<Map<String, Object>> getSystemFilmWatchingHistory(@Param("userId") String userId);

    @Query(value = "SELECT DISTINCT \n" +
            "    w.film_id, \n" +
            "    DATE(w.watching_time) AS watching_date, \n" +
            "    tf.video_key,\n" +
            "    tf.tmdb_id,\n" +
            "    w.watched_duration\n" +
            "FROM watching AS w\n" +
            "JOIN tmdb_film AS tf ON w.film_id = tf.id\n" +
            "ORDER BY DATE(w.watching_time) DESC\n" +
            "LIMIT 20;", nativeQuery = true)
    List<Map<String, Object>> getTmdbFilmWatchingHistory(@Param("userId") String userId);

    List<Watching> findByUser_IdAndFilm_FilmId(String userId, String filmId);
}
