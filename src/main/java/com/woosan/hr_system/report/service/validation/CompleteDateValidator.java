package com.woosan.hr_system.report.service.validation;

import com.woosan.hr_system.report.model.Report;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class CompleteDateValidator implements ConstraintValidator<ValidCompleteDate, Report> {
    @Override
    public boolean isValid(Report report, ConstraintValidatorContext context) {
        // 시작일과 종료일이 null이 아닌 경우에만 검사
        if (report.getCompleteDate() != null && report.getCompleteDate().isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("완료일이 오늘이거나 그 이전이어야 합니다.")
                    .addPropertyNode("completeDate")  // 여기에 검증할 필드 이름 추가
                    .addConstraintViolation();
            return false;
        }
        // 유효성 검사 통과 시 true 반환
        return true;
    }
}
