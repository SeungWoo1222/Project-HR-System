package com.woosan.hr_system.auth;

import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeDAO employeeDAO;

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
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

    @GetMapping("/auth/pwd") // 비밀번호 검증 페이지 이동
    public String viewPasswordForm(@RequestParam("employeeId") String employeeId, @RequestParam("redirectUrl") String redirectUrl, Model model) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("redirectUrl", redirectUrl);
        return "/auth/pwd";
    }

    @PostMapping("/auth/verifyPassword") // 비밀번호 인증 로직
    public ResponseEntity<String> verifyPassword(@RequestParam String password, @RequestParam String url) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        String result = authService.verifyPassword(password, employeeId);
        return switch (result) {
            case "match" -> ResponseEntity.ok(url + "/" + employeeId);
            case "mismatch" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("비밀번호가 틀렸습니다.\n" + "현재 시도 횟수 : " + employeeDAO.getPasswordCount(employeeId) + " / 5 입니다.");
            case "exceed" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 오류 횟수 초과입니다. 관리자에게 문의해주세요.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 확인 중 오류가 발생했습니다. 관리자에게 문의해주세요.");
        };
    }
}
