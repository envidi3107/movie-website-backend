package com.example.MovieWebsiteProject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotNull
    @NotEmpty
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters!")
    private String username;

    @NotNull
    @NotEmpty
    @Email(message = "Email is invalid!")
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters!")
    private String password;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private MultipartFile avatarFile;
}
