package com.back.banka.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY= "secret_key";

    private final long EXPIRATION_TIME = 1000*60*60; //Tiempo de una hora

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// 1 hora
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            System.out.println("Token expired:" + e.getMessage());
        } catch (JwtException e){
            System.out.println("Invalid token" + e.getMessage());
        }
        return false;
    }
}
