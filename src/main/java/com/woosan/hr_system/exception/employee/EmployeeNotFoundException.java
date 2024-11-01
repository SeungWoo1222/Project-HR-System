package com.woosan.hr_system.exception.employee;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String employeeId) {
        super("해당 사원을 찾을 수 없습니다.\n사원 ID : " + employeeId);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}