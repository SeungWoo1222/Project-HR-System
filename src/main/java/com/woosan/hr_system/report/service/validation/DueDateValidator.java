package com.woosan.hr_system.report.service.validation;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DueDateValidator implements ConstraintValidator<ValidDueDate, Request> {
    @Override
    public boolean isValid(Request request, ConstraintValidatorContext context) {
        // 시작일과 종료일이 null이 아닌 경우에만 검사
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("마감 기한은 오늘이거나 그 이후이어야 합니다.")
                    .addPropertyNode("dueDate")  // 여기에 검증할 필드 이름 추가
                    .addConstraintViolation();
            return false;
        }
        // 유효성 검사 통과 시 true 반환
        return true;
    }
}
