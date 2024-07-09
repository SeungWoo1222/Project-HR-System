package com.woosan.hr_system.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/index") // 로그인 페이지 이동
    public String home() {
        return "/index";
    }
    @GetMapping("/auth/login") // 로그인 페이지 이동
    public String login() {
        return "/auth/login";
    }
}
