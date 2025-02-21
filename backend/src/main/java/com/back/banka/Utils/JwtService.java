//package com.back.banka.Utils;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//@Service
//public class JwtService {
//
//
//        @Value("${jwt.secret}")
//        private  String secretKey;
//
//        @Value("${jwt.expiration}")
//        private  int expirationTime ;
//
//
///**
// * Metodo para generar un token de jwt recibe el usuario autenticado
// * */
//        public String generateToken(Authentication authentication){
//            UserDetails mainUser = (UserDetails) authentication.getPrincipal();
//            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//            return Jwts.builder().setSubject(mainUser.getUsername())
//                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(new Date().getTime() + expirationTime * 1000L))
//                    .signWith(key, SignatureAlgorithm.HS256)
//                    .compact();
//        }
//
//
//
//
//    /**
//     * Metodo para extraer claims del payload del usuario
//     * */
//        public Claims extractAllClaims(String token){
//            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//            return Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//        }
//
//
//    /**
//     * Metodo para validar si el token a expirado
//     * */
//        public Boolean validateToken(String token, UserDetails userDetails){
//            final String userName = extractUserName(token);
//            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//        }
//
//    /**
//     * Metodo para extraer el username del token
//     * */
//        public String extractUserName(String token){
//            return extractAllClaims(token).getSubject();
//        }
//    /**
//     * Metodo para extraer el tiempo de expiracion
//     * */
//        public Date extractExpiration(String token){
//            return extractAllClaims(token).getExpiration();
//        }
//    /**
//     * Metodo  para cuando u token a expirado
//     * */
//        public Boolean isTokenExpired(String token){
//            return extractExpiration(token).before(new Date());
//        }
//    }
//
//
