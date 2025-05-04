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

    @Query(value = """
            WITH recent_days AS (
                SELECT DISTINCT DATE(watching_time) AS day
                FROM watching
                WHERE watching_time < NOW()
                ORDER BY day DESC
                LIMIT 20
            ),
            grouped_watch AS (
                SELECT 
                    DATE(watching_time) AS day,
                    HOUR(watching_time) AS hour,
                    COUNT(DISTINCT user_id) AS user_count
                FROM watching
                WHERE DATE(watching_time) IN (SELECT day FROM recent_days)
                GROUP BY day, hour
            ),
            ranked_watch AS (
                SELECT *,
                       RANK() OVER (PARTITION BY day ORDER BY user_count DESC) AS rnk
                FROM grouped_watch
            )
            SELECT 
                day,
                hour AS start_hour,
                hour + 1 AS end_hour,
                user_count
            FROM ranked_watch
            WHERE rnk = 1
            ORDER BY day DESC, start_hour;
            """, nativeQuery = true)
    List<Object[]> findMostPopularHoursPerDay();

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
            "WHERE w.user_id = :userId\n" +
            "ORDER BY DATE(w.watching_time) DESC\n" +
            "LIMIT 20;", nativeQuery = true)
    List<Map<String, Object>> getSystemFilmWatchingHistory(@Param("userId") String userId);

    @Query(value = "SELECT DISTINCT \n" +
            "    w.film_id, \n" +
            "    DATE(w.watching_time) AS watching_date, \n" +
            "    tf.tmdb_id,\n" +
            "    w.watched_duration\n" +
            "FROM watching AS w\n" +
            "JOIN tmdb_film AS tf ON w.film_id = tf.id\n" +
            "WHERE w.user_id = :userId\n" +
            "ORDER BY DATE(w.watching_time) DESC\n" +
            "LIMIT 20;", nativeQuery = true)
    List<Map<String, Object>> getTmdbFilmWatchingHistory(@Param("userId") String userId);

    List<Watching> findByUser_IdAndFilm_FilmId(String userId, String filmId);
}
