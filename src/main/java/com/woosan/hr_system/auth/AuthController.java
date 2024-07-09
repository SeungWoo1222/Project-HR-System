package com.woosan.hr_system.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @GetMapping("/auth/login") // 로그인 페이지 이동
    public String login() {
        return "/auth/login";
    }

    @PostMapping("/auth/login") // 로그인 로직
    public String authenticate(@RequestParam("id") String id,
                               @RequestParam("password") String password) {
        // 로그인 성공 시 메인 페이지로 리다이렉트
        return "redirect:/index";
        // 로그인 실패 시 로그인 페이지로 리다이렉트
        return "redirect:/auth/login?error";
    }
}
