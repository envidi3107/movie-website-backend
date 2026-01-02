package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Repository.InvalidatedTokenRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.Dto.projection.UserAuthInfo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Getter
    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    public String generateToken(UserAuthInfo user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("phimhayyy.envidi.com")
                    .issueTime(new Date())
                    .expirationTime(
                            new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                    )
                    .claim("role", user.getRole())
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
