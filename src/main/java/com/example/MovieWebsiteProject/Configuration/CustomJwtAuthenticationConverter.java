package com.example.MovieWebsiteProject.Configuration;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Enum.ErrorCode;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final UserRepository userRepository;

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {

    String userId = jwt.getClaim("userId");
    if (userId == null) {
      throw new AppException(ErrorCode.EXPIRED_LOGIN_SESSION);
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTES));

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

    return new UsernamePasswordAuthenticationToken(user, jwt, List.of(authority));
  }
}
