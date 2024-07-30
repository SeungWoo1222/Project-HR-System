package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private PasswordDAO passwordDAO;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        String employeeId = request.getParameter("username");

        if (exception instanceof BadCredentialsException) {
            if (employeeId != null) { passwordDAO.incrementPasswordCount(employeeId); }
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다.";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "계정이 만료되었습니다.";
        } else if (exception instanceof LockedException) {
            errorMessage = "계정이 잠겼습니다.";
        } else {
            errorMessage = "알 수 없는 오류로 로그인 요청을 처리할 수 없습니다.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"message\": \"" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()) + "\"}");
        writer.flush();
        writer.close();
    }
}
