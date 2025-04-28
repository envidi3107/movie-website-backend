package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.dto.projection.UserAuthInfo;
import com.example.MovieWebsiteProject.dto.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String userId);

    // sử dụng JPQL truy vấn trên User Entity để ánh xạ kết quả trả về sang UserResponse

    @Query("SELECT new com.example.MovieWebsiteProject.dto.response.UserResponse(" +
            "u.id, u.username, u.email, u.role, u.createdAt, u.avatarPath, " +
            "u.ipAddress, u.country, u.dateOfBirth) FROM User u WHERE u.role = 'USER' ORDER BY u.createdAt DESC")
    List<UserResponse> getAllUser();


    @Query(value = "SELECT DATE_FORMAT(u.created_at, '%Y-%m') AS month, COUNT(id) AS totalUsers " +
            "FROM user u " +
            "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> countNewUsersPerMonth();

    @Query(value = "SELECT u.id, u.username, u.password, u.email, u.role FROM user u WHERE u.email = :email", nativeQuery = true)
    Optional<UserAuthInfo> findUsernameAndPasswordByEmail(@Param("email") String email);
}
