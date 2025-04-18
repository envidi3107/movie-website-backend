package com.example.IdentityService.dto.request;

import jakarta.persistence.Column;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters!")
    @NonNull
    private String username;

    @Email(message = "Email is invalid!")
    @NonNull
    private String email;

    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters!")
    @NonNull
    private String password;
}
