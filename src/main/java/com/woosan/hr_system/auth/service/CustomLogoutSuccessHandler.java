package com.woosan.hr_system.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            log.info("'{}' 사원이 로그아웃하였습니다.", authentication.getName());
        } else {
            log.info("익명 사용자이거나 인증 객체(null)이 로그아웃하였습니다.");
        }

        // 로그아웃 후 리다이렉션 할 URL 설정
        response.sendRedirect("/auth/logout");
    }
}
