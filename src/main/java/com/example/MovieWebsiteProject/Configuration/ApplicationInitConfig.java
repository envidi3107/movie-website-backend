package com.example.MovieWebsiteProject.Configuration;

import com.example.MovieWebsiteProject.Common.Roles;
import com.example.MovieWebsiteProject.Entity.Playlist;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Repository.PlaylistRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, PlaylistRepository playlistRepository) {

        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                LocalDateTime accessTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy HH:mm:ss", Locale.UK);
                User user = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .createdAt(accessTime)
                        .role(Roles.ADMIN.name())
                        .build();
                userRepository.save(user);
            }
            if (playlistRepository.findByPlaylistName("Yêu thích").isEmpty()) {
                Playlist playlist = Playlist.builder()
                        .playlistName("Yêu thích")
                        .build();
                playlistRepository.save(playlist);
            }
            if (playlistRepository.findByPlaylistName("Xem sau").isEmpty()) {
                Playlist playlist = Playlist.builder()
                        .playlistName("Xem sau")
                        .build();
                playlistRepository.save(playlist);
            }
        };
    }
}
