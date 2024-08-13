package com.woosan.hr_system.auth.service;

import com.woosan.hr_system.auth.model.CustomUserDetails;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.exception.employee.PasswordNotFoundException;
import org.springframework.security.core.AuthenticationException;

public interface AuthService {
    Password getPasswordInfoById(String employeeId);
    CustomUserDetails getAuthenticatedUser();
    int verifyPasswordAttempts(String password, String employeeId);
    void insertPassword(String employeeId, String birth) throws IllegalArgumentException;
    String changePassword(String employeeId, String currentPassword, String newPassword, int strength) throws IllegalArgumentException, PasswordNotFoundException, AuthenticationException;
    void updatePassword(String employeeId, String newPassword, int strength) throws PasswordNotFoundException;
    String isPasswordChangeRequired();
    void verifyManagerPermission();
    void verifyDepartment(String department);
    void deletePassword(String employeeId);
    String setAccountLock(String employeeId);
}
