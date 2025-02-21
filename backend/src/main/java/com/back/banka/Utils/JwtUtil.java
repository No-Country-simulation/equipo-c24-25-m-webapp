package com.back.banka.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtUtil {

    private final Key key;
    private final long expirationTime;

    public JwtUtil(@Value("${JWT_SECRET}") String secret, @Value("${JWT_EXPIRATION}")long expiration){
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); //Generando clave con HS256
        this.expirationTime = expiration;
    }


    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())//Fecha de creación del token
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))// Tiempo en que va a expirar el Token
                .signWith(key, SignatureAlgorithm.HS256)//Se usa HS256
                .compact();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e){
            System.out.println("Token expirado:" + e.getMessage());
        } catch (JwtException e){
            System.out.println("Token Inválido" + e.getMessage());
        }
        return false;
    }

    //Para la validación del token

    public String ValidateTokenAndGetMessage(String token){
        return validateToken(token)?"Token válido":"Token inválido";
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
