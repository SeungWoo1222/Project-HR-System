package com.woosan.hr_system.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/auth/login") // 로그인 인증 로직
    public String loginProcess() {
        return "/home";
    }

    @GetMapping("/auth/logout") // 로그아웃 로직
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("logoutMessage", "로그아웃 되었습니다.");
        return "redirect:/auth/login";
    }
}
