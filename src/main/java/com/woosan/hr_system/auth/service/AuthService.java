package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordDAO passwordDAO;

    // 비밀번호 입력 최대 개수
    private static final int MAX_ATTEMPTS = 5;

    // 비밀번호 패턴
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]:\";'<>?,./`~])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]:\";'<>?,./`~]{8,20}$";

    // 현재 로그인된 사원의 userDetails 조회 로직
    public CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        return null;
    }

    // 비밀번호 카운트 조회 메소드
    public Boolean isExceeded (String employeeId) {
        return passwordDAO.getPasswordCount(employeeId) >= MAX_ATTEMPTS;
    }

    // 비밀번호 검증 로직
    public boolean verifyPassword(String enteredPassword, String employeeId) {
        // 비밀번호 일치
        if (passwordEncoder.matches(enteredPassword, passwordDAO.selectPassword(employeeId).getPassword())) {
            passwordDAO.resetPasswordCount(employeeId);
            return true;
        }
        // 비밀번호 불일치
        passwordDAO.incrementPasswordCount(employeeId);
        return false;

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

    // 비밀번호 수정 전 확인 로직
    public String changePassword(String employeeId, String password, String newPassword, int strength) {
        // 현재 비밀번호와 새로운 비밀번호 비교
        if (password.equals(newPassword)) return "equal";

        // 비밀번호 패턴 검증
        if (!validatePassword(newPassword)) return "invalid";

        updatePassword(employeeId, newPassword, strength);
        return "success";
    }

    // 비밀번호 변경 로직
    public void updatePassword(String employeeId, String newPassword, int strength) {
        Password pwd = passwordDAO.selectPassword(employeeId);
        if (pwd == null) {
            throw new IllegalArgumentException("Password record not found for employeeId: " + employeeId);
        }
        pwd.setPassword(passwordEncoder.encode(newPassword));
        pwd.setLastModified(LocalDateTime.now());
        pwd.setModifiedBy(employeeId);
        pwd.setStrength(strength);
        passwordDAO.updatePassword(pwd);
    }

    // 비밀번호 패턴 검증 로직
    private boolean validatePassword(String password) {
        return password.matches(PASSWORD_PATTERN);
    }

    // 비밀번호 만료 여부 확인 로직
    public String isPasswordChangeRequired() {
        LocalDateTime lastModifiedDateTime = passwordDAO.selectPassword(getAuthenticatedUser().getUsername()).getLastModified();

        // 첫 비밀번호 변경 확인
        if (lastModifiedDateTime == null) return "FirstChangeRequired";

        // 비밀번호 변경 후 3개월이 지났는지 확인
        LocalDate lastModifiedDate = lastModifiedDateTime.toLocalDate();
        LocalDate now = LocalDate.now();
        if (now.isAfter(lastModifiedDate.plusMonths(3))) {
            return "ChangeRequired";
        }
        return "NoChangeRequired";
    }

    // 현재 로그인 계정의 사원 권한 확인하는 메소드
    public String verifyManagerPermission() {
        Collection<? extends GrantedAuthority> authorities = getAuthenticatedUser().getAuthorities();

        // 관리자 권한 확인
        boolean hasManagerRole = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));

        if (!hasManagerRole) return "error/403";
        return null;
    }

    // 현재 로그인 계정의 부서 확인하는 메소드
    public String verifyDepartment(String department) {
        if (getAuthenticatedUser().getDepartment().equals(department)) return "error/403";
        return null;
    }
}
