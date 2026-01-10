package com.example.MovieWebsiteProject.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieWebsiteProject.Dto.projection.UserAuthInfo;
import com.example.MovieWebsiteProject.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findById(String userId);

  @Query(
      value =
          """
            SELECT DATE_FORMAT(u.created_at, '%Y-%m') AS month, COUNT(id) AS totalUsers
            FROM user u
            GROUP BY month ORDER BY month
            """,
      nativeQuery = true)
  List<Object[]> countNewUsersPerMonth();

  @Query(
      value =
          "SELECT u.id, u.username, u.password, u.email, u.role FROM user u WHERE u.email = :email",
      nativeQuery = true)
  Optional<UserAuthInfo> findUsernameAndPasswordByEmail(@Param("email") String email);
}
