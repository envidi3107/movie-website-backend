package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Common.Roles;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.dto.request.UserCreationRequest;
import com.example.MovieWebsiteProject.dto.request.UserUpdateRequest;
import com.example.MovieWebsiteProject.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationService authenticationService;

    public void createUser(UserCreationRequest request, HttpServletRequest httpServletRequest) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        LocalDateTime accessTime = LocalDateTime.now();
        user.setCreatedAt(accessTime);
        user.setIpAddress(getClientIp(httpServletRequest));
        user.setCountry(httpServletRequest.getLocale().getCountry());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(Roles.USER.name());
        userRepository.save(user);
    }

    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @PostAuthorize("returnObject != null && returnObject.getUsername() == authentication.getName()")
    public User getUserInfo() {
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new RuntimeException("User info not found!"));
    }

    public void updateUser(UserUpdateRequest request) {
        User user = getUser(authenticationService.getAuthenticatedUser().getId());

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MUST_BE_DIFFERENCE);
        } else {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setDateOfBirth(request.getDateOfBirth());

        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            try {
                // Đặt tên file (có thể thêm timestamp hoặc UUID để tránh trùng)
                String fileName = UUID.randomUUID() + "_" + request.getAvatarFile().getOriginalFilename();
                Path uploadDir = Paths.get("uploads/avatars");

                // Tạo thư mục nếu chưa tồn tại
                Files.createDirectories(uploadDir);

                // Ghi file vào thư mục
                Path filePath = uploadDir.resolve(fileName);
                Files.write(filePath, request.getAvatarFile().getBytes());

                // Set đường dẫn file vào avatar (có thể là URL tương đối hoặc tuyệt đối)
                user.setAvatarPath("/uploads/avatars/" + fileName);
                
                userRepository.save(user);
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
