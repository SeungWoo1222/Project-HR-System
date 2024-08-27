package com.woosan.hr_system.exception.salary;

public class SalaryNotFoundException extends RuntimeException {
    public SalaryNotFoundException(Object id) {
        super(generateMessage(id));
    }

    private static String generateMessage(Object id) {
        if (id instanceof Integer) {
            return "해당 사원의 급여 정보를 찾을 수 없습니다.\n급여 ID : " + id;
        } else if (id instanceof String) {
            return "해당 사원의 급여 정보를 찾을 수 없습니다.\n사원 ID : " + id;
        } else {
            return "해당 사원의 급여 정보를 찾을 수 없습니다.\n알 수 없는 ID : " + id;
        }
    }

    public SalaryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}