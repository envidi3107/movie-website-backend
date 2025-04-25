package com.example.MovieWebsiteProject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username, email, role;
    private LocalDateTime createAt;
    private String avatarPath;
    private String avatarName, ipAddress, country;
    private LocalDate dateOfBirth;
}
