package com.woosan.hr_system.schedule.service.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 클래스 전체에 적용
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ScheduleDateValidator.class)
public @interface ValidScheduleDates {
    String message() default "종료일이 시작일보다 이전일 수 없습니다.";
    Class<?>[] groups() default {};
}
