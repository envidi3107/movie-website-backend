package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.Reaction.Reaction;
import com.example.MovieWebsiteProject.Entity.Reaction.ReactionID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, ReactionID> {
    @Query(value = "SELECT r.film_id, r.reaction_type, r.reaction_time FROM reaction AS r WHERE user_id = :userId", nativeQuery = true)
    List<String[]> getUserReaction(@Param("userId") String userId);

    @Query(value = "SELECT * FROM reaction WHERE user_id = :userId AND film_id = :filmId", nativeQuery = true)
    Optional<Reaction> getReactionByUserIdAndFilmId(@Param("userId") String userId, @Param("filmId") String filmId);

    @Query(value = """
            select r.user_id, u.username, u.email, u.created_at, u.avatar_path, count(r.film_id) AS total_likes
            from reaction AS r
            JOIN user AS u ON u.id = r.user_id
            where r.reaction_type = 'LIKE'
            GROUP BY r.user_id, u.username, u.email, u.created_at, u.avatar_path
            ORDER BY total_likes desc
            LIMIT :limit
            """, nativeQuery = true)
    List<Map<String, Object>> getTopUserLike(@Param("limit") int limit);
}
