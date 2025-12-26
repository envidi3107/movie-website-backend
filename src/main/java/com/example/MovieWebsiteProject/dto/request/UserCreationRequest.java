package com.example.MovieWebsiteProject.Dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters!")
    @NotNull
    private String username;

    @Email(message = "Email is invalid!")
    @NotNull
    private String email;

    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters!")
    @NotNull
    private String password;

    @NotNull
    private LocalDate dateOfBirth;
}
