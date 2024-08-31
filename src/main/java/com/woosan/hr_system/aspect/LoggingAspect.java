package com.woosan.hr_system.aspect;

import com.woosan.hr_system.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Autowired
    private AuthService authService;

    // 메서드에 대한 실행 전 로깅
    @Before("@annotation(com.woosan.hr_system.aspect.LogBeforeExecution)")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("메소드 진입: {}", joinPoint.getSignature().toShortString());
    }

    // 메서드에 대한 실행 후 로깅
    @AfterReturning(pointcut = "@annotation(com.woosan.hr_system.aspect.LogAfterExecution)", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        if (result == null) {
            log.info("메소드 종료: {} / (처리자: {})", joinPoint.getSignature().toShortString(), authService.getAuthenticatedUser().getNameWithId());
        } else {
            log.info("메소드 종료: {} / 결과: {} (처리자: {})", joinPoint.getSignature().toShortString(), result, authService.getAuthenticatedUser().getNameWithId());
        }
    }
}
