package com.example.MovieWebsiteProject.Dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordUpdateRequest {
    @NotNull
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters!")
    String password;
}
