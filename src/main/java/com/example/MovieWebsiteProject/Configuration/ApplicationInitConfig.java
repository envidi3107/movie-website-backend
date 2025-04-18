package com.example.IdentityService.Configuration;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.UserRoles.Roles;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {

        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                LocalDateTime accessTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy HH:mm:ss", Locale.UK);
                User user = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .createdAt(accessTime)
                        .role(Roles.ADMIN.name())
                        .build();
                userRepository.save(user);
            }
        };
    }
}
