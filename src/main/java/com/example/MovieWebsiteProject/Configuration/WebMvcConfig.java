package com.example.MovieWebsiteProject.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("*")); // Chỉ định origin
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true); // Cho phép gửi cookie/token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:uploads/avatars/"); // Đường dẫn thực tế trên server
    }
}
