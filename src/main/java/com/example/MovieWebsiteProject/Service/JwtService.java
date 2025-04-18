package com.example.IdentityService.Service;

import com.example.IdentityService.Entity.User;
import com.example.IdentityService.Exception.AppException;
import com.example.IdentityService.Exception.ErrorCode;
import com.example.IdentityService.Repository.InvalidatedTokenRepository;
import com.example.IdentityService.Repository.UserRepository;
import com.example.IdentityService.dto.projection.UserAuthInfo;
import com.example.IdentityService.dto.request.IntrospectRequest;
import com.example.IdentityService.dto.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Getter
    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    // Tạo chuỗi scope (role)
    private String buildScope(UserAuthInfo user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRole() != null) {
            stringJoiner.add(user.getRole());
        }

        return stringJoiner.toString();
    }

    // Tạo JWT
    public String generateToken(UserAuthInfo user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("phimhayyy.envidi.com")
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 day
                    .claim("scope", buildScope(user))
                    .claim("userId", user.getId())
                    .jwtID(UUID.randomUUID().toString())
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate token", e);
        }
    }

    // Trích xuất username
    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean isValid = signedJWT.verify(verifier);
        boolean notExpired = signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());

        if (!(isValid && notExpired)) {
            throw new AppException(ErrorCode.EXPIRED_LOGIN_SESSION);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return signedJWT;
    }
}
