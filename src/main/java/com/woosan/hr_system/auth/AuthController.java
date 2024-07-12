package com.woosan.hr_system.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @GetMapping("/home") // 홈페이지 이동
    public String home() {
        return "/home";
    }
    @GetMapping("/auth/login") // 로그인 페이지 이동
    public String login() {
        return "/auth/login";
    }
    @PostMapping("/auth/login") // 로그인 인증
    public String loginProcess() {
        return "/home";
    }


}
