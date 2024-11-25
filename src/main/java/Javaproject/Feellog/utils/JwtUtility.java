package Javaproject.Feellog.utils;

import Javaproject.Feellog.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtUtility {

    private final String secret="yourSecretKey";

    private static final long EXPIRATION_TINE = 1000L * 60 * 60 * 24;

    public String generateToken(String id){
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TINE))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Claims validateToken(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch(SignatureException | ExpiredJwtException e){
            throw new InvalidTokenException("유효하지 않은 토큰");
        }catch(Exception e){
            throw new InvalidTokenException("토큰 검증 중 오류 발생");
        }
    }

    public String bearerToken(String token){
        if(token != null){
            return token.replace("Bearer","");
        }else{
            throw new InvalidTokenException("유효하지 않은 토큰");
        }
    }
}
