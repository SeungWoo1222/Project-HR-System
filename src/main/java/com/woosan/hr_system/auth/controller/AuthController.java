package com.woosan.hr_system.auth.controller;

import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login") // 로그인 페이지 이동
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "message", required = false) String message,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "/auth/login";
    }

    @GetMapping("/logout") // 로그아웃 로직
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("logoutMessage", "로그아웃 되었습니다.");
        return "redirect:/auth/login";
    }

    @GetMapping("/session-expired") // 세션 만료시 페이지 이동
    public String expireSession() {
        return "/auth/session-expired";
    }

    @GetMapping("/pwd") // 비밀번호 검증 페이지 이동
    public String viewPasswordForm(@RequestParam("redirectUrl") String redirectUrl, Model model) {
        model.addAttribute("redirectUrl", redirectUrl);
        return "/auth/pwd";
    }

    @PostMapping("/verifyPassword") // 비밀번호 인증 로직
    public ResponseEntity<String> verifyPassword(@RequestParam("password") String password, @RequestParam("url") String url) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        int message = authService.verifyPasswordAttempts(password, employeeId);
        return switch (message) {
            case -1 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 오류 횟수 초과로 계정이 차단되었습니다.\n관리자에게 문의해주세요.");
            case 0 -> ResponseEntity.ok(url + employeeId);
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.\n" + "현재 시도 횟수 : " + message + " / 5 입니다.");
        };
    }

    @GetMapping("/pwd-management") // 비밀번호 관리 페이지 이동
    public String viewPasswordManagement(Model model) {
        Password password = authService.getPasswordInfoById(authService.getAuthenticatedUser().getUsername());
        model.addAttribute("password", password);
        return "/auth/pwd-management";
    }

    @GetMapping("/pwd-change") // 비밀번호 변경 페이지 이동
    public String viewPasswordChangeForm(@RequestParam(value = "message", required = false) String message, Model model) {
        if (message != null) model.addAttribute("message", message);
        return "/auth/pwd-change";
    }

    @PutMapping("/changePassword") // 비밀번호 변경 로직
    public ResponseEntity<String> updatePassword(@RequestParam("password") String password, @RequestParam("new-password") String newPassword, @RequestParam("strength") int strength) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        int message = authService.verifyPasswordAttempts(password, employeeId);

        return switch (message) {
            case -1 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 오류 횟수 초과로 계정이 차단되었습니다.\n관리자에게 문의해주세요.");
            case 0 -> {
                // 현재 비밀번호와 새로운 비밀번호 비교 후 비밀번호 수정
                authService.changePassword(employeeId, password, newPassword, strength);
                yield ResponseEntity.ok("비밀번호가 변경되었습니다.\n다시 로그인해주세요.");
            }
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.\n" + "현재 시도 횟수 : " + message + " / 5 입니다.");
        };
    }
}
