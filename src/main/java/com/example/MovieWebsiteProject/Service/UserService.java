package com.example.IdentityService.Service;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.UserRoles.Roles;
import com.example.IdentityService.dto.request.UserCreationRequest;
import com.example.IdentityService.dto.request.UserUpdateRequest;
import com.example.IdentityService.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request, HttpServletRequest httpServletRequest) {
        if(userRepository.existsByUsername(request.getUsername())) {
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
        user.setRole(Roles.USER.name());
        return userRepository.save(user);
    }

    public User getUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        return user;
    }

    @PostAuthorize("returnObject != null && returnObject.getUsername() == authentication.getName()")
    public User getUserInfo() {
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new RuntimeException("User info not found!"));
    }

    public void updateUser(String userId, UserUpdateRequest request) {
        User user = getUser(userId);
        if (user.getPassword().equals(request.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MUST_BE_DIFFERENCE);
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.updateUser(user, request);
        userRepository.save(user);
    }

    public void updateUserPassword(String userId, String newPassword) {
        User user = getUser(userId);
        if(passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MUST_BE_DIFFERENCE);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public String uploadUserAvatar(String userId, MultipartFile file) throws IOException {
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTES));

        user.setAvatarName(file.getOriginalFilename());
        user.setAvatarData(file.getBytes());
        userRepository.save(user);
        return "Updated avatar successfully!";
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
