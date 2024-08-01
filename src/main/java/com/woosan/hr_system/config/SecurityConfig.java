package com.woosan.hr_system.config;

import com.woosan.hr_system.auth.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 요청에 대한 인가 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/employee/registration", "/auth/login", "/error/**","/css/**", "/js/**", "/images/**", "/files/**").permitAll() // 이 경로는 인증 없이 접근 허용

                                // 일반 사원 권한
//                                .requestMatchers("/").hasAnyRole("사원", "대리", "과장")
                                // 관리자 권한
                                .requestMatchers("/admin/**").hasAnyRole("차장", "부장", "사장")

                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                        //      .anyRequest().permitAll() // 모든 요청에 대해 인증 없이 접근 허용
                )

                // 폼 기반 인증 설정
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/auth/login") // 사용자 정의 로그인 페이지 경로
                                .defaultSuccessUrl("/home", true) // 로그인 성공 시 리다이렉트될 경로
                                .permitAll() // 로그인 페이지는 인증 없이 접근 허용
                )

                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // 로그아웃 URL
                                .logoutSuccessUrl("/auth/logout") // 로그아웃 성공 시 리다이렉트될 경로
                                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                                .permitAll() // 로그아웃 URL은 인증 없이 접근 허용
                )

                // Session
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)// 필요 시 세션 생성
                                .sessionFixation().migrateSession() // 세션 고정 보호 설정
                                .maximumSessions(1) // 하나의 세션만 허용
                                .maxSessionsPreventsLogin(true)
                                .expiredUrl("/auth/expired") // 세션 만료 시 리다이렉트할 URL
                )

                // CSRF 보호 설정
                .csrf(csrf -> csrf.disable()) // RESTful API 사용으로 CSRF 보호 비활성화

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
