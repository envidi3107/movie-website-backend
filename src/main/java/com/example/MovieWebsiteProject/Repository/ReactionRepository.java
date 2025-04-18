package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.Reaction.Reaction;
import com.example.IdentityService.Entity.Reaction.ReactionID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, ReactionID> {
    @Query(value = "SELECT r.film_id, r.reaction_type FROM reaction AS r WHERE user_id = :userId", nativeQuery = true)
    List<String[]> getReaction(@Param("userId") String userId);
}
