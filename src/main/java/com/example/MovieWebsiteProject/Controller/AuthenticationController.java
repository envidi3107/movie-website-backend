package com.example.MovieWebsiteProject.Controller;

import java.text.ParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieWebsiteProject.Dto.request.AuthenticationRequest;
import com.example.MovieWebsiteProject.Dto.response.ApiResponse;
import com.example.MovieWebsiteProject.Dto.response.AuthenticationResponse;
import com.example.MovieWebsiteProject.Dto.response.IntrospectResponse;
import com.example.MovieWebsiteProject.Enum.SuccessCode;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.JwtService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    JwtService jwtService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
                                                                     @RequestBody AuthenticationRequest request, HttpServletResponse response) {
        var results = authenticationService.authenticate(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder().code(SuccessCode.LOG_IN_SUCCESSFULLY.getCode()).message(SuccessCode.LOG_IN_SUCCESSFULLY.getMessage()).results(results).build());
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(
                                             HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParseException {

        authenticationService.logout(request);
        // Xóa session
        session.invalidate();

        // Xóa cookie accessToken và JSESSIONID
        ResponseCookie clearAccessTokenCookie = ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessTokenCookie.toString());

        ResponseCookie clearSessionCookie = ResponseCookie.from("JSESSIONID", "").path("/").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearSessionCookie.toString());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder().code(SuccessCode.LOG_OUT_SUCCESSFULLY.getCode()).message(SuccessCode.LOG_OUT_SUCCESSFULLY.getMessage()).build());
    }

    @PostMapping("/authenticate-password")
    ApiResponse<IntrospectResponse> authenticatePassword(@RequestBody AuthenticationRequest request) throws ParseException, JOSEException {
        authenticationService.authenticateUserAccount(request);
        return ApiResponse.<IntrospectResponse>builder().code(SuccessCode.SUCCESS.getCode()).message(SuccessCode.SUCCESS.getMessage()).results(IntrospectResponse.builder().valid(true).build()).build();
    }
}
