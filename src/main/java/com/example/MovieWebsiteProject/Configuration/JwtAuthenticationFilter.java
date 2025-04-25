package com.example.MovieWebsiteProject.Configuration;

import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Service.JwtService;
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
                String userId = signedJWT.getJWTClaimsSet().getClaims().get("userId").toString();
                signedJWT.getJWTClaimsSet().getClaims().forEach((k, v) -> {
                    System.out.println("key = " + k + ", value = " + v);
                });
                User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTES));
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Gán vào SecurityContext để xác thực các request sau
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                // Token invalid or expired
                System.out.println("Token verification failed: " + e.getMessage());

                // Xóa context để đảm bảo không có xác thực cũ
                SecurityContextHolder.clearContext();

                // Trả về lỗi (bạn có thể dùng response.sendError hoặc custom JSON response)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                    {
                      "code": "401",
                      "message": "Phiên đăng nhập đã hết hạn hoặc token không hợp lệ."
                    }
                """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
