package com.woosan.hr_system.auth;

import com.woosan.hr_system.employee.dao.EmployeeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmployeeDAO employeeDAO;

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
        String hashedPassword = passwordEncoder.encode(enteredPassword);
        if (employeeDAO.getPasswordCount(employeeId) >= 5) { // 비밀번호 카운트 초과
            return "exceed";
        } else if (getAuthenticatedUser().getPassword().equals(hashedPassword)) { // 비밀번호 일치
            employeeDAO.removePasswordCount(employeeId);
            return "match";
        } else { // 비밀번호 불일치
            employeeDAO.addPasswordCount(employeeId);
            return "mismatch";
        }
    }
}
