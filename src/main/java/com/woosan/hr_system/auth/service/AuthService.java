package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordDAO passwordDAO;

    // 현재 로그인된 사원의 userDetails 조회 로직
    public CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        return null;
    }

    // 비밀번호 검증 로직
    public String verifyPassword(String enteredPassword, String employeeId) {
        if (passwordDAO.getPasswordCount(employeeId) >= 5) { // 비밀번호 카운트 초과
            return "exceed";
        } else if (passwordEncoder.matches(enteredPassword, passwordDAO.selectPassword(employeeId).getPassword())) { // 비밀번호 일치
            passwordDAO.removePasswordCount(employeeId);
            return "match";
        } else { // 비밀번호 불일치
            passwordDAO.addPasswordCount(employeeId);
            return "mismatch";
        }
    }

    // 첫 비밀번호 등록
    public String insertFirstPassword(String employeeId, String birth) {
        if (employeeId.isEmpty() || birth.isEmpty()) { // 입력 검증
            return "null";
        }
        Password password = new Password();
        password.setEmployeeId(employeeId);
        password.setPassword(passwordEncoder.encode(birth)); // 첫 비밀번호는 생년월일 6자리로 설정
        passwordDAO.insertPassword(password);
        return "success";
    }
}
