package com.example.IdentityService.Configuration;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;
        System.out.println("doFilterInternal is working");
        Cookie[] cookies = request.getCookies();
        // 🔍 Lấy JWT từ Cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : cookies) {
                System.out.println("cookie in request header: " + cookie.getName() + ", " + cookie.getValue());
                if (cookie.getName().equals("accessToken")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null) {
            try {
                SignedJWT signedJWT = jwtService.verifyToken(jwt);
                String username = signedJWT.getJWTClaimsSet().getSubject();
                signedJWT.getJWTClaimsSet().getClaims().forEach((k, v) -> {
                    System.out.println("key = " + k + ", value = " + v);
                });
                User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTES));
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Gán vào SecurityContext để xác thực các request sau
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                System.out.println("Token verification failed: " + e.getMessage());
                throw new AppException(ErrorCode.EXPIRED_LOGIN_SESSION);
            }
        }

        filterChain.doFilter(request, response);
    }
}
