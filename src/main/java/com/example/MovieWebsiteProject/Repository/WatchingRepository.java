package com.example.MovieWebsiteProject.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Entity.Watching;

@Repository
public interface WatchingRepository extends JpaRepository<Watching, String> {

  @Query(
      value =
          """
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
            """,
      nativeQuery = true)
  List<Object[]> findMostPopularHoursPerDay();

  @Query(
      value =
          """
            SELECT
                w.film_id,
                sf.title,
                sf.backdrop_path,
                sf.poster_path,
                sf.video_path,
                sf.total_durations,
                w.watching_time,
                w.watched_duration
            FROM watching w
            JOIN (
                SELECT film_id, MAX(watching_time) AS latest_time
                FROM watching
                WHERE user_id = :userId
                GROUP BY film_id
            ) latest_watching ON w.film_id = latest_watching.film_id AND w.watching_time = latest_watching.latest_time
            JOIN system_film AS sf ON w.film_id = sf.system_film_id
            WHERE w.user_id = :userId
            ORDER BY w.watching_time DESC
            LIMIT :limit;
            """,
      nativeQuery = true)
  List<Map<String, Object>> getSystemFilmWatchingHistory(
      @Param("userId") String userId, @Param("limit") int limit);

  @Query(
      value =
          """
            SELECT
                w.film_id,
                tf.tmdb_id,
                w.watching_time
            FROM watching w
            JOIN (
                SELECT film_id, MAX(watching_time) AS latest_time
                FROM watching
                WHERE user_id = :userId
                GROUP BY film_id
            ) latest_watching ON w.film_id = latest_watching.film_id AND w.watching_time = latest_watching.latest_time
            JOIN tmdb_film AS tf ON w.film_id = tf.id
            WHERE w.user_id = :userId
            ORDER BY w.watching_time DESC
            LIMIT :limit;
            """,
      nativeQuery = true)
  List<Map<String, Object>> getTmdbFilmWatchingHistory(
      @Param("userId") String userId, @Param("limit") int limit);

  @Query(
      value =
          "SELECT * FROM watching WHERE film_id = :filmId AND user_id = :userId ORDER BY watching_time DESC LIMIT 1",
      nativeQuery = true)
  Optional<Watching> findNewWatchingByUserIdAndFilmId(
      @Param("userId") String userId, @Param("filmId") String filmId);
}
