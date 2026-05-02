package com.example.MovieWebsiteProject.Configuration;

import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.EXPIRED_LOGIN_SESSION.getCode());
        apiResponse.setMessage(ErrorCode.EXPIRED_LOGIN_SESSION.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}
