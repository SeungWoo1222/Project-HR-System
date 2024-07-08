//package com.woosan.hr_system.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests(authorizeRequests ->
//                        authorizeRequests
//                                .antMatchers("/", "/index", "/public/**").permitAll() // 이 경로는 인증 없이 접근 허용
//                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
//                )
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/login") // 로그인 페이지 경로
//                                .defaultSuccessUrl("/home", true) // 로그인 성공 시 리다이렉트될 경로
//                                .permitAll() // 로그인 페이지는 인증 없이 접근 허용
//                )
//                .logout(logout ->
//                        logout
//                                .logoutUrl("/logout") // 로그아웃 URL
//                                .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 리다이렉트될 경로
//                                .permitAll() // 로그아웃 URL은 인증 없이 접근 허용
//                )
//                .rememberMe(rememberMe ->
//                        rememberMe
//                                .key("uniqueAndSecret") // Remember-Me 키 설정
//                                .tokenValiditySeconds(1209600) // Remember-Me 토큰의 유효 기간 설정 (2주)
//                )
//                .csrf(csrf ->
//                        csrf
//                                .ignoringAntMatchers("/h2-console/**") // 특정 경로에서 CSRF 보호 비활성화
//                )
//                .headers(headers ->
//                        headers
//                                .frameOptions().sameOrigin() // 동일 출처에서만 프레임 로딩 허용
//                                .contentSecurityPolicy("default-src 'self'") // Content Security Policy 설정
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
