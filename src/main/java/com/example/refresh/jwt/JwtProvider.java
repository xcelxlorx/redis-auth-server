package com.example.refresh.jwt;

import com.example.refresh.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    private static String secret;

    @Getter
    private static Long accessExpires;

    @Getter
    private static Long refreshExpires;

    @Value("${jwt.secret}")
    private void setSecret(String value){
        secret = value;
    }

    @Value("${jwt.token.access-expires}")
    private void setAccessExpires(Long value){
        accessExpires = value;
    }

    @Value("${jwt.token.refresh-expires}")
    private void setRefreshExpires(Long value){
        refreshExpires = value;
    }

    public static String createAccessToken(Member member){
        Claims claims = Jwts.claims().setSubject(member.getId().toString());
        Date now = getNow();
        Date expiration = getExpiration(now, accessExpires);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TOKEN_PREFIX + jwt;
    }

    public static String createRefreshToken(Member member){
        Claims claims = Jwts.claims().setSubject(member.getId().toString());
        Date now = getNow();
        Date expiration = getExpiration(now, refreshExpires);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Date getNow() {
        return new Date();
    }

    private static Date getExpiration(Date now, Long expires) {
        return new Date(now.getTime() + expires);
    }
}
