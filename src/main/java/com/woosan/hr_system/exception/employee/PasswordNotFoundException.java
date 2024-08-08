package com.woosan.hr_system.exception.employee;

public class PasswordNotFoundException extends RuntimeException {
    public PasswordNotFoundException(String employeeId) {
        super("비밀번호 정보를 찾을 수 없습니다.\n사원 ID : " + employeeId);
    }
}

