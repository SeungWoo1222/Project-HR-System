package com.woosan.hr_system.exception.employee;

public class ResignationNotFoundException extends RuntimeException{
    public ResignationNotFoundException(String employeeId) {
        super("퇴사 정보를 찾을 수 없습니다.\n사원 ID : " + employeeId);
    }

    public ResignationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
