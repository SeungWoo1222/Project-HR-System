package com.woosan.hr_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 요청에 대한 인가 설정
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/*", "/css/**", "/js/**", "/file/**", "/images/**").permitAll() // 이 경로는 인증 없이 접근 허용
                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                )

                // 폼 기반 인증 설정
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/auth/login") // 사용자 정의 로그인 페이지 경로
                                .defaultSuccessUrl("/index", true) // 로그인 성공 시 리다이렉트될 경로
                                .permitAll() // 로그인 페이지는 인증 없이 접근 허용
                )

                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // 로그아웃 URL
                                .logoutSuccessUrl("/index") // 로그아웃 성공 시 리다이렉트될 경로
                                .permitAll() // 로그아웃 URL은 인증 없이 접근 허용
                )

                // Session
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)// 필요 시 세션 생성
                                .sessionFixation().migrateSession() // 세션 고정 보호 설정
                                .maximumSessions(1) // 하나의 세션만 허용
                                .maxSessionsPreventsLogin(true)
                                // session 만료 > 현재 페이지에서 session 만료 알림창 > login 페이지로 이동으로 구현 예정
                                .expiredUrl("/auth/login?expired") // 세션 완료 시 리다이렉트할 URL
                )

                // CSRF 보호 설정 - Restful API 사용으로 비활성화
//                .csrf(csrf ->
//                        csrf
//                                .ignoringAntMatchers("/h2-console/**") // 특정 경로에서 CSRF 보호 비활성화
//                )

                // 헤더 설정
                .headers(headers ->
                        headers
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 동일 출처에서만 프레임 로딩 허용
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
