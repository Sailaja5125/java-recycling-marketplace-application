package com.example.application.Utils;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JWTUtils {
////    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private final SecretKey key = Jwts.SIG.HS256.key().build();
//    private final long expirationMs = 3600000; // 1 hour
//
//        public String generateToken(String email) {
//
//            return Jwts.builder()
//                    .subject(email)
//                    .claim("email" ,email)
//                    .issuedAt(new Date())
//                    .expiration(new Date(System.currentTimeMillis()+expirationMs))
//                    .signWith(key)
//                    .compact();
//
//        }
//        public Claims extractClaims(String token){
//            return Jwts.parser().setSigningKey(key).build().parseEncryptedClaims(token).getPayload();
//        }
//        public String extractEmail(String token) {
//            return extractClaims(token).getSubject();
//        }
//        public String getEmail(String token){
//            return (String) extractClaims(token).get("email");
//        }
//    public boolean isTokenValid(String token) {
//        try {
//            extractClaims(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//
//}
//
//
//
//

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {

    private static final String SECRET = "thisisaverylongsecretkeyformyjwtandmustbeatleast32chars";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));



    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("email",email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        return extractClaims(token).getSubject();
    }


    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
