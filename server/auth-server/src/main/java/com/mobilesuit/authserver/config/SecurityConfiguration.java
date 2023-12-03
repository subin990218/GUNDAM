package com.mobilesuit.authserver.config;

import com.mobilesuit.authserver.auth.handler.*;
import com.mobilesuit.authserver.auth.jwt.JwtAuthenticationFilter;
import com.mobilesuit.authserver.auth.jwt.JwtTokenizer;
import com.mobilesuit.authserver.auth.jwt.JwtVerificationFilter;
import com.mobilesuit.authserver.auth.redis.RedisDao;
import com.mobilesuit.authserver.auth.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final RedisDao redisDao;
    //private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                /*.formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())

                .and()
                .authorizeHttpRequests(authorize->authorize
                        //.requestMatchers(HttpMethod.POST,"/h2-console/*").permitAll()
                        //.requestMatchers(toH2Console()).permitAll() // H2 Console 에 대한 모든 접근을 허용하기 위한 메서드
                        //.requestMatchers(HttpMethod.POST,"/h2-console/*").permitAll()
                        *//*.requestMatchers("/static/*").permitAll()
                        .requestMatchers("/model/*").permitAll()
                        .requestMatchers("/assets/*").permitAll()
                        .requestMatchers("/oauth2/*").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/reissue").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/members/register**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/members/list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/fishes/catch").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/members/*").hasRole("USER")
                        .requestMatchers(HttpMethod.POST).authenticated()*//*
                        .anyRequest().permitAll()
                )*/
                .oauth2Login()
                .loginPage("/login/github")
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint();
                //.userInfoEndpoint(); //일반적으로 OAuth 2.0 및 OpenID Connect 프로토콜에서 인증 성공 후 인증된 사용자에 대한 추가 정보를 얻기 위해 사용됩니다.
                //.userService(customOAuth2UserService);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("http://localhost:7070");
        configuration.addAllowedOriginPattern("https://accounts.google.com");
        //configuration.addAllowedOrigin("https://i9E105.p.ssafy.io");
        configuration.addAllowedOriginPattern("*:3000");
        configuration.addExposedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(
                    AuthenticationManager.class
            );
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                    authenticationManager, jwtTokenizer, redisDao
            );

            jwtAuthenticationFilter.setFilterProcessesUrl("/api/login");

            jwtAuthenticationFilter.setAuthenticationSuccessHandler(
                    new MemberAuthenticationSuccessHandler()
            );

            jwtAuthenticationFilter.setAuthenticationFailureHandler(
                    new MemberAuthenticationFailureHandler()
            );

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(
                    jwtTokenizer, authorityUtils
            );
            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }

}
