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

    public Claims validateToken(String token) {
        try {
            System.out.println("검증할 토큰: " + token); // 디버깅 로그 추가
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            System.err.println("토큰 만료: " + e.getMessage()); // 만료 로그 출력
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        } catch (SignatureException e) {
            System.err.println("서명 오류: " + e.getMessage()); // 서명 오류 로그 출력
            throw new InvalidTokenException("유효하지 않은 서명입니다.");
        } catch (JwtException e) {
            System.err.println("JWT 예외: " + e.getMessage()); // JWT 관련 예외 처리
            throw new InvalidTokenException("JWT 검증 중 오류 발생");
        } catch (Exception e) {
            System.err.println("알 수 없는 예외 발생: " + e.getMessage()); // 기타 예외 처리
            throw new InvalidTokenException("토큰 검증 중 알 수 없는 오류 발생");
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
