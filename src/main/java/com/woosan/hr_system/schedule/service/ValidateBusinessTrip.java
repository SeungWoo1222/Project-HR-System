package com.woosan.hr_system.schedule.service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})  // 클래스 레벨에서 사용할 것이므로 TYPE으로 지정
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BusinessTripValidator.class)  // 실제 검증 로직을 담당할 클래스 지정
public @interface ValidateBusinessTrip {
    String message() default "출장 정보가 있을 경우 모두 입력해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
