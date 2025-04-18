package com.example.IdentityService.Service;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.Repository.WatchingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {
    UserRepository userRepository;
    WatchingRepository watchingRepository;


    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getMonthlyNewUsers() {
        List<Object[]> results = userRepository.countNewUsersPerMonth();
        List<Map<String, Object>> response = new ArrayList<>();

        results.forEach(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("mounth", row[0]);
            map.put("total_users", row[1]);
            response.add(map);
        });
        return response;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getUsersWatchingPerHour(String dateTime) {
        var results = watchingRepository.countUsersWatchingPerHour(dateTime);
        List<Map<String, Object>> response = new ArrayList<>();
        results.forEach(row -> {
            Map<String, Object> data = new HashMap<>();
            data.put("watch_hour", row[1]);
            data.put("total_users", row[0]);
            response.add(data);
        });
        return response;
    }
}
