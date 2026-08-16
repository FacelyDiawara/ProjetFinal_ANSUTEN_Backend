package com.example.backend_facely.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.key=Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret)); this.expirationMs=expirationMs;
    }
    public String generateToken(String username, String role){
        Date now=new Date();
        return Jwts.builder().subject(username).claim("role", role).issuedAt(now).expiration(new Date(now.getTime()+expirationMs)).signWith(key).compact();
    }
    public String extractUsername(String token){return extractClaim(token, Claims::getSubject);}
    public boolean isTokenValid(String token,String username){try{return username.equals(extractUsername(token)) && !isExpired(token);}catch(JwtException|IllegalArgumentException e){return false;}}
    private boolean isExpired(String token){return extractClaim(token,Claims::getExpiration).before(new Date());}
    private <T>T extractClaim(String token, Function<Claims,T> resolver){return resolver.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());}
}
