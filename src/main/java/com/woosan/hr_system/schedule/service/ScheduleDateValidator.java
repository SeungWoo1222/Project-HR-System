package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.model.Schedule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ScheduleDateValidator implements ConstraintValidator<ValidScheduleDates, Schedule> {
    @Override
    public boolean isValid(Schedule schedule, ConstraintValidatorContext context) {
        // 시작일과 종료일이 null이 아닌 경우에만 검사
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("시작 일과 종료 일을 모두 입력해주세요.")
                    .addConstraintViolation();
            return false;
        }

        // 종료일이 시작일보다 이전이 아니어야 함
        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("시작일이 종료일보다 빠르거나 같아야 합니다.")
                    .addConstraintViolation();
            return false;
        }

        // 유효성 검사 통과 시 true 반환
        return true;
    }
}
