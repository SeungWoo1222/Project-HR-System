package com.woosan.hr_system.auth.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ExceptionHandlingAspect { // 예외 처리를 위한 AOP Aspect
    @AfterThrowing(pointcut = "execution(* com.woosan.hr_system.employee.service..*(..))", throwing = "ex")
    public void handleDataAccessException(DataAccessException ex) {
        log.error("데이터베이스 오류가 발생했습니다: {}", ex.getMessage(), ex);
        throw new RuntimeException("데이터베이스 오류가 발생했습니다. 관리자에게 문의하세요.", ex);
    }

    @AfterThrowing(pointcut = "execution(* com.woosan.hr_system.employee.service..*(..))", throwing = "ex")
    public void handleGeneralException(Exception ex) {
        log.error("알 수 없는 오류가 발생했습니다: {}", ex.getMessage(), ex);
        throw new RuntimeException("오류가 발생했습니다. 관리자에게 문의하세요.", ex);
    }
}
