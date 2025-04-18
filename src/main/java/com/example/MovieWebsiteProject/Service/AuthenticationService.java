package com.example.IdentityService.Service;

import com.example.IdentityService.Entity.InvalidatedToken;
import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.InvalidatedTokenRepository;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.dto.projection.UserAuthInfo;
import com.example.IdentityService.dto.request.AuthenticationRequest;
import com.example.IdentityService.dto.request.IntrospectRequest;
import com.example.IdentityService.dto.response.AuthenticationResponse;
import com.example.IdentityService.dto.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    JwtService jwtService;

    public UserAuthInfo authenticateUserAccount(AuthenticationRequest request) {
        UserAuthInfo user = userRepository.findUsernameAndPasswordByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTES));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        } else {
            return user;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        boolean authenticated = false;

        UserAuthInfo user = authenticateUserAccount(request);
        authenticated = true;
        String token = "null";
        try {
            token = jwtService.generateToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Server error!, " + e.getMessage());
        }
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            jwtService.verifyToken(request.getToken());
        } catch (Exception e) {
            System.out.println("Lỗi khi verify token: " + e.getMessage());
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void logout(String token) throws ParseException, JOSEException {
        var signedToken = jwtService.verifyToken(token);
        String jwtID = signedToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtID)
                .expiryTime(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        System.out.println("principal: " + authentication.getPrincipal());
        throw new RuntimeException("User cannot authenticated");
    }
}
