package com.woosan.hr_system.config;

import com.woosan.hr_system.auth.service.CustomAuthenticationEntryPoint;
import com.woosan.hr_system.auth.service.CustomAuthenticationFailureHandler;
import com.woosan.hr_system.auth.service.CustomAuthenticationSuccessHandler;
import com.woosan.hr_system.auth.service.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 요청에 대한 인가 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/login", "/auth/logout","/auth/session-expired", "/error/**","/css/**", "/js/**", "/images/**", "/files/**", "/api/employee/update").permitAll() // 이 경로는 인증 없이 접근 허용

                                // 관리자 권한
                                .requestMatchers("/admin/**", "/api/admin/**").hasAnyRole("MANAGER")

                                // 일반 사원 권한
                                .requestMatchers("/**").hasAnyRole("STAFF", "MANAGER")

                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
//                              .anyRequest().permitAll() // 모든 요청에 대해 인증 없이 접근 허용
                )

                // 폼 기반 인증 설정
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/auth/login") // 사용자 정의 로그인 페이지 경로
                                .defaultSuccessUrl("/employee/list", true)
                                .successHandler(customAuthenticationSuccessHandler()) // 커스텀 성공 핸들러 설정
                                .failureHandler(customAuthenticationFailureHandler()) // 커스텀 실패 핸들러 설정
                                .permitAll() // 로그인 페이지는 인증 없이 접근 허용
                )
                // 새로운 방식의 HTTP Basic 인증 활성화
                .httpBasic(httpBasic ->
                        httpBasic
                                .realmName("MyAppRealm") // 원하는 Realm 이름 설정
                )
                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // 로그아웃 URL
                                .logoutSuccessHandler(new CustomLogoutSuccessHandler()) // 로그아웃 커스텀 성공 핸들러 설정
                                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                                .permitAll() // 로그아웃 URL은 인증 없이 접근 허용
                )

                // Session
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)// 필요 시 세션 생성
                                .sessionFixation().migrateSession() // 세션 고정 보호 설정
                                .invalidSessionUrl("/auth/session-expired") // 세션이 유효하지 않을 때 리다이렉트할 URL
                                .maximumSessions(1) // 하나의 세션만 허용
                                .maxSessionsPreventsLogin(true)
                                .expiredUrl("/auth/session-expired")  // 세션 만료 시 리다이렉트할 URL

                )

                // CSRF 보호 설정
                .csrf(csrf -> csrf.disable()) // RESTful API 사용으로 CSRF 보호 비활성화

                // 헤더 설정
                .headers(headers ->
                        headers
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 동일 출처에서만 프레임 로딩 허용
                )

                // Authentication Entry Point 설정 - 401
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
}
