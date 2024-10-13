package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.model.BusinessTrip;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessTripValidator implements ConstraintValidator<ValidateBusinessTrip, BusinessTrip> {
    @Override
    public boolean isValid(BusinessTrip businessTrip, ConstraintValidatorContext context) {
        // 출장지 정보가 입력된 경우
        if (isNotBlank(businessTrip.getAddress()) || isNotBlank(businessTrip.getDetailedAddress())) {
            // 나머지 필드 중 하나라도 null이거나 공백일 경우 false 반환
            if (isBlank(businessTrip.getTripName()) || isBlank(businessTrip.getContactTel()) || isBlank(businessTrip.getContactEmail())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("출장 정보가 있을 경우 모두 입력해주세요.")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    // 공백 체크 메소드 추가
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
