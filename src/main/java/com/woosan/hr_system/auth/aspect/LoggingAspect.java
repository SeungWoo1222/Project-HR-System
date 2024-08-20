package com.woosan.hr_system.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    // 메서드에 대한 실행 전 로깅
    @Before("@annotation(com.woosan.hr_system.auth.aspect.LogBeforeExecution)")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("메소드 진입: {}", joinPoint.getSignature().toShortString());
    }

    // 메서드에 대한 실행 후 로깅
    @AfterReturning(pointcut = "@annotation(com.woosan.hr_system.auth.aspect.LogAfterExecution)", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        if (result == null) {
            log.info("메소드 종료: {} / (처리자: {})", joinPoint.getSignature().toShortString(), SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            log.info("메소드 종료: {} / 결과: {} (처리자: {})", joinPoint.getSignature().toShortString(), result, SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }
}
