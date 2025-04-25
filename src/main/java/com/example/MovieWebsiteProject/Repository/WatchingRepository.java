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

    @Query(value = "SELECT COUNT(DISTINCT user_id) AS total_users, watch_hour, date(watch_time) as watch_date\n" +
            "FROM watching\n" +
            "GROUP BY watch_hour, watch_time\n" +
            "HAVING watch_date = :watchDate\n" +
            "ORDER BY watch_hour DESC", nativeQuery = true)
    List<Object[]> countUsersWatchingPerHour(@Param("watchDate") String dateTime);

    @Query(value = "SELECT DISTINCT \n" +
            "    w.film_id, \n" +
            "    film.belong_to, \n" +
            "    sf.title, \n" +
            "    sf.video_path, \n" +
            "    DATE(w.watching_time) AS watching_date, \n" +
            "    ft.video_key, \n" +
            "    ft.tmdb_id, \n" +
            "    film.watched_duration\n" +
            "FROM watching AS w\n" +
            "JOIN film ON w.film_id = film.film_id\n" +
            "JOIN system_film AS sf ON film.film_id = sf.system_film_id\n" +
            "JOIN film_trailers as ft ON film.film_id = ft.film_trailers_id" +
            "WHERE w.user_id = :userId\n" +
            "ORDER BY DATE(w.watching_time) DESC LIMIT 20;\n", nativeQuery = true)
    List<Map<String, Object>> getFilmWatchingHistory(@Param("userId") String userId);

}
