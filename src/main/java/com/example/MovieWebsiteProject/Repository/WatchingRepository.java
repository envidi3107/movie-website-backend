package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.Film;
import com.example.IdentityService.Entity.Watching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface WatchingRepository extends JpaRepository<Watching, String> {

    @Query(value = "SELECT COUNT(user_id) AS Total_users, watch_hour, date(time_stamp) as date_time\n" +
            "FROM watching\n" +
            "GROUP BY watch_hour, date_time\n" +
            "HAVING date_time = :dateTime\n" +
            "ORDER BY watch_hour asc", nativeQuery = true)
    List<Object[]> countUsersWatchingPerHour(@Param("dateTime") String dateTime);

    @Query(value = "SELECT DISTINCT w.film_id, film.number_of_views, film.number_of_likes, film.number_of_dislikes, film.belong_to, sf.adult, sf.title, sf.release_date, sf.overview, sf.backdrop_path, sf.poster_path, sf.video_path, DATE(w.watch_time) as watch_date, w.watch_time\n" +
            "FROM watching AS w\n" +
            "JOIN film ON w.film_id = film.film_id\n" +
            "JOIN system_film AS sf ON film.film_id = sf.system_film_id\n" +
            "WHERE w.user_id = :userId\n" +
            "ORDER BY w.watch_time DESC", nativeQuery = true)
    List<Map<String, Object>> getFilmWatchingHistory(@Param("userId") String userId);
}
