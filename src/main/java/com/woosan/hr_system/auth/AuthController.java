package com.woosan.hr_system.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {
    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }
//    @GetMapping("/home") // 홈페이지 이동
//    public String home(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        model.addAttribute("username", userDetails.getUsername());
//        model.addAttribute("password", userDetails.getPassword());
//        model.addAttribute("authorities", userDetails.getAuthorities());
//        model.addAttribute("department", userDetails.getDepartment());
//        return "/home";
//    }

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
