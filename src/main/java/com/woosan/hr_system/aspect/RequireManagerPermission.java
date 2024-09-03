package com.woosan.hr_system.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 런타임 시에 어노테이션 유지
@Target(ElementType.METHOD) // 메소드에만 적용
public @interface RequireManagerPermission {
}
