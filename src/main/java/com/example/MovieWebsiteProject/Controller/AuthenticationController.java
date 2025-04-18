package com.example.IdentityService.Controller;

import com.example.IdentityService.Common.SuccessCode;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Service.AuthenticationService;
import com.example.IdentityService.Service.JwtService;
import com.example.IdentityService.dto.request.IntrospectRequest;
import com.example.IdentityService.dto.request.LogoutRequest;
import com.example.IdentityService.dto.response.ApiResponse;
import com.example.IdentityService.dto.request.AuthenticationRequest;
import com.example.IdentityService.dto.response.AuthenticationResponse;
import com.example.IdentityService.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    JwtService jwtService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        var result = authenticationService.authenticate(request);

        ResponseCookie cookie = ResponseCookie.from("accessToken", result.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        result.setToken(null);
        return ResponseEntity.ok(
            ApiResponse.<AuthenticationResponse>builder()
                .code(SuccessCode.LOG_IN_SUCCESSFULLY.getCode())
                .message(SuccessCode.LOG_IN_SUCCESSFULLY.getMessage())
                .result(result)
                .build()
        );
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParseException, JOSEException {
        String token = "";
        
        authenticationService.logout(token);

        // Xóa cookie session
        session.invalidate();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/"); // Đặt phạm vi cookie
        cookie.setHttpOnly(true); // Đảm bảo cookie không thể truy cập từ JavaScript
        cookie.setMaxAge(0); // Xóa cookie bằng cách đặt thời gian tồn tại bằng 0
        response.addCookie(cookie);

        return ApiResponse.<Void>builder()
                .code(SuccessCode.LOG_OUT_SUCCESSFULLY.getCode())
                .message(SuccessCode.LOG_OUT_SUCCESSFULLY.getMessage())
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/authenticate-password")
    ApiResponse<IntrospectResponse> authenticatePassword(@RequestHeader("Authorization") String token, @RequestBody AuthenticationRequest request) throws ParseException, JOSEException {
        IntrospectRequest introspectRequest = new IntrospectRequest(token.substring(7));
        IntrospectResponse introspectResponse = authenticationService.introspect(introspectRequest);
        boolean isCorrect = false;
        if (introspectResponse.getValid()) {
            authenticationService.authenticateUserAccount(request);
            isCorrect = true;
        } else {
            throw new AppException(ErrorCode.EXPIRED_LOGIN_SESSION);
        }
        return ApiResponse.<IntrospectResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .result(IntrospectResponse.builder()
                        .valid(isCorrect)
                        .build())
                .build();
    }
}
