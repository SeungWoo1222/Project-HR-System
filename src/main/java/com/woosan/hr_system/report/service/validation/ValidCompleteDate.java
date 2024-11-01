package com.woosan.hr_system.report.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 클래스 전체에 적용
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CompleteDateValidator.class)
public @interface ValidCompleteDate {
    String message() default "완료일이 오늘이거나 그 이전이어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {}; // payload 추가
}
