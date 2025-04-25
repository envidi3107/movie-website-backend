package com.example.MovieWebsiteProject.Configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.example.MovieWebsiteProject.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.MovieWebsiteProject.dto.request.IntrospectRequest;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        var response = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build());

        if (!response.getValid()) throw new JwtException("Token invalid");

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(jwtService.getSIGNER_KEY().getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}