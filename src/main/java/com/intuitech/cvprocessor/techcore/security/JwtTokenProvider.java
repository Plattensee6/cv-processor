package com.intuitech.cvprocessor.techcore.security;

import com.intuitech.cvprocessor.domain.auth.Role;
import com.intuitech.cvprocessor.domain.auth.User;
import com.intuitech.cvprocessor.techcore.config.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfigProperties jwtConfigProperties;

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtConfigProperties.getAccessTokenTtlSeconds());

        SecretKey key = Keys.hmacShaKeyFor(jwtConfigProperties.getSecret().getBytes());

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer(jwtConfigProperties.getIssuer())
                .setAudience(jwtConfigProperties.getAudience())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("roles", roles)
                .claim("fullName", user.getFullName())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfigProperties.getSecret().getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfigProperties.getSecret().getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(email, token, authorities);
    }
}

