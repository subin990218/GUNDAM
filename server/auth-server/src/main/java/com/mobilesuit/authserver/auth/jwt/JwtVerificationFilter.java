package com.mobilesuit.authserver.auth.jwt;

import com.mobilesuit.authserver.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            ee.printStackTrace();
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");
        return authorization ==null || !authorization.startsWith("Bearer");
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {

        //  request 의 header 에서 JWT 를 얻어오기
        String jws = request.getHeader("authorization").replace("Bearer ", "");

        //  JWT 서명(Signature)을 검증하기 위한 Secret Key 를 얻습니다.
        String base64EncodedSecretKey = jwtTokenizer.encodeBaseSecretKey(
                jwtTokenizer.getSecretKey());

        //  JWT 에서 Claims 를 파싱합니다.
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }

    //  setAuthenticationToContext() = Authentication 객체를 SecurityContext 에 저장하기 위한 메서드
    private void setAuthenticationToContext(Map<String, Object> claims) {
        System.out.println("######################################################################");
        List<String> roles = (List<String>) claims.get("roles");
        for(String temp:roles) System.out.println(temp);
        System.out.println((String)claims.get("userEmail"));

        //  JWT 에서 파싱한 Claims 에서 username 을 얻습니다.
        String username = (String) claims.get("userEmail");

        //  JWT 의 Claims 에서 얻은 권한 정보를 기반으로 List<GrantedAuthority 를 생성합니다.
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities(
                (List) claims.get("roles"));
        //  username 과 List<GrantedAuthority 를 포함한 Authentication 객체를 생성합니다.
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                authorities);

        //  SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
