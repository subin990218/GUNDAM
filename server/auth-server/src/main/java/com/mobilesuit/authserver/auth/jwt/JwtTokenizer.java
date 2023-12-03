package com.mobilesuit.authserver.auth.jwt;


import com.mobilesuit.authserver.auth.redis.RedisDao;
import com.mobilesuit.authserver.exception.BusinessLogicException;
import com.mobilesuit.authserver.exception.ExceptionCode;
import com.mobilesuit.authserver.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class JwtTokenizer {
    private final RedisDao redisDao;

    @Getter
    @Value("${jwt.secret-key}")
    private String secretKey;


    @Getter
    @Value("${jwt.access-token-expiration-sec}")
    private int accessTokenExpirationSecs;

    @Getter
    @Value("${jwt.refresh-token-expiration-sec}")
    private int refreshTokenExpirationSecs;

    public String encodeBaseSecretKey(String secretKey){
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(Map<String,Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodeSecretKey){
        Key key = getKeyFrombase64EncodedKey(base64EncodeSecretKey);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Map<String,Object> claims,
                                       String subject,
                                       Date expiration,
                                       String base64EncodeSecretKey){
        Key key = getKeyFrombase64EncodedKey(base64EncodeSecretKey);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey){
        Key key = getKeyFrombase64EncodedKey(base64EncodedSecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(jws);
        return claims;
    }

    public Date getTokenExpiration(int expirationSecs){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expirationSecs);
        Date expiration = calendar.getTime();
        return expiration;
    }






    /*----------------------------protected Method를 Public으로 전환해서 Oauth2.0 Success handler 처리-------------------------------*/

    public String delegateAccessToken(Member member) {
        //System.out.println("액세스 토큰 만들러왔음");
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getMemberId());
        claims.put("userEmail", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = getTokenExpiration(
                getAccessTokenExpirationSecs());

        String base64EncodedSecretKey = encodeBaseSecretKey(
                getSecretKey());

        String accessToken = generateAccessToken(claims, subject, expiration,
                base64EncodedSecretKey);

        return accessToken;
    }

    //      Access Token 과 Refresh Token 을 생성하는 구체적인 로직
    public String delegateRefreshToken(Member member) {
        //claims 에 속성값 추가
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getMemberId());

        String subject = member.getEmail();
        Date expiration = getTokenExpiration(
                getRefreshTokenExpirationSecs());
        String base64EncodedSecretKey = encodeBaseSecretKey(
                getSecretKey());

        String refreshToken = generateRefreshToken(claims, subject, expiration,
                base64EncodedSecretKey);

        return refreshToken;
    }

    private Claims parseRefreshToken(String token){
        Key key = getKeyFrombase64EncodedKey(encodeBaseSecretKey(secretKey));
        String jws = token.replace("refreshToken=", "");
        Claims claims;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();
        }   catch (ExpiredJwtException e) {
            e.printStackTrace();
            System.out.println("만료된 리프레시 토큰");
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }
        return claims;
    }

    private Claims parseToken(String token) {
        Key key = getKeyFrombase64EncodedKey(encodeBaseSecretKey(secretKey));
        String jws = token.replace("Bearer ", "");
        Claims claims;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();
        }   catch (ExpiredJwtException e) {
            e.printStackTrace();
            System.out.println("만료된 액세스 토큰");
            throw new BusinessLogicException(ExceptionCode.INVALID_ACCESS_TOKEN);
        }
        System.out.println("ID : "+ claims.get("memberId"));
        return claims;
    }

    // token 에서 memberId 추출
    public Long getMemberId(String token) {
        long memberId = parseToken(token).get("memberId", Long.class);
        return memberId;
    }

    public Long getMemberIdFromRefresh(String token){
        long memberId = parseRefreshToken(token).get("memberId",Long.class);
        return memberId;
    }

    public void validRefreshToken(String token, Member member){
        System.out.println("##################=########################=################");
        System.out.println(redisDao.getValues(member.getEmail()));
        System.out.println(token);
        char[] cmp = redisDao.getValues(member.getEmail()).toCharArray();
        char[] org = token.toCharArray();



        if(org.length != cmp.length) throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        for(int i =0; i< org.length;i++){
            if(cmp[i]!=org[i]) throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }
    }

    // redis 에 저장된 refreshToken 삭제
    public void deleteRtk(Member member) throws JwtException {
        redisDao.deleteValues(member.getEmail());
    }

    // accessToken 재발급
    public String reissueAtk(Member member) throws JwtException {
        String atk = delegateAccessToken(member);
        return atk;
    }

    // refreshToken 재발급
    public String reissueRtk(Member member) throws JwtException {
        String rtk = delegateRefreshToken(member);
        redisDao.setValues(member.getEmail(), rtk, Duration.ofSeconds(getRefreshTokenExpirationSecs()));
        return rtk;
    }

    private Key getKeyFrombase64EncodedKey(String base64EncodedSecretKey){
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }


}
