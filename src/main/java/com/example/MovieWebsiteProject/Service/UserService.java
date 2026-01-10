package com.example.MovieWebsiteProject.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.MovieWebsiteProject.Dto.request.UserCreationRequest;
import com.example.MovieWebsiteProject.Dto.request.UserUpdateRequest;
import com.example.MovieWebsiteProject.Dto.response.UserResponse;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Enum.Roles;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  AuthenticationService authenticationService;
  private final PlaylistRepository playlistRepository;

  @Value("${app.base_url}")
  @NonFinal
  private String baseUrl;

  public void createUser(UserCreationRequest request, HttpServletRequest httpServletRequest) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USERNAME_EXISTED);
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AppException(ErrorCode.EMAIL_EXISTED);
    }
    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .createdAt(LocalDateTime.now())
            .country(httpServletRequest.getLocale().getCountry())
            .role(Roles.USER.name())
            .ipAddress(getClientIp(httpServletRequest))
            .dateOfBirth(request.getDateOfBirth())
            .build();

    user = userRepository.save(user);

    // Tạo playlist mặc định
    List<Playlist> defaultPlaylists =
        List.of(
            Playlist.builder()
                .playlistName("Yêu thích")
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build(),
            Playlist.builder()
                .playlistName("Xem sau")
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build());

    // Lưu playlist
    playlistRepository.saveAll(defaultPlaylists);
  }

  public User getUser(String id) {
    return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
  }

  @PostAuthorize("returnObject != null && returnObject.getUsername() == authentication.getName()")
  public User getUserInfo() {
    var context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();

    return userRepository
        .findByUsername(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User info not found!"));
  }

  public UserResponse updateUser(UserUpdateRequest request) {
    User user = getUser(authenticationService.getAuthenticatedUser().getId());

    if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
      try {
        String fileName =
            UUID.randomUUID().toString()
                + "."
                + Objects.requireNonNull(request.getAvatarFile().getContentType()).split("/")[1];
        Path uploadDir = Paths.get("uploads/avatars");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(fileName);

        // Xóa avatar cũ nếu tồn tại
        if (user.getAvatarPath() != null) {
          Path oldAvatarPath =
              Paths.get("uploads", user.getAvatarPath().replaceFirst("^/uploads/", ""));
          if (Files.exists(oldAvatarPath)) {
            Files.delete(oldAvatarPath);
          }
        }

        // Ghi file mới
        Files.write(filePath, request.getAvatarFile().getBytes());
        user.setAvatarPath("/uploads/avatars/" + fileName);

        user = userRepository.save(user);

        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .avatarPath(baseUrl + user.getAvatarPath())
            .dateOfBirth(user.getDateOfBirth())
            .build();
      } catch (IOException e) {
        throw new RuntimeException("Không thể lưu avatar", e);
      }
    } else {
      throw new AppException(ErrorCode.FILE_IS_INVALID);
    }
  }

  public void updateUserPassword(String newPassword) {
    User user = getUser(authenticationService.getAuthenticatedUser().getId());
    if (passwordEncoder.matches(newPassword, user.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_MUST_BE_DIFFERENCE);
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
      System.out.println("Proxy-Client-IP = " + ip);
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
      System.out.println("WL-Proxy-Client-IP = " + ip);
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
      System.out.println("remote addr = " + ip);
    }

    // Nếu có nhiều IP (do qua proxy), lấy cái đầu tiên
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0];
      System.out.println("ip proxy = " + ip);
    }

    return ip;
  }
}
