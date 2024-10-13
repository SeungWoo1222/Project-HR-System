package com.woosan.hr_system.schedule.model;

import com.woosan.hr_system.schedule.service.ValidateBusinessTrip;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ValidateBusinessTrip  // 커스텀 유효성 검사 - 출장지
public class BusinessTrip {
    private int tripId;
    private int taskId;
    private String address;
    private String detailedAddress;
    private LocalDateTime createdDate;
    private String status;
    private String tripName;

    @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4})$", message = "유효한 휴대전화 번호를 입력해주세요.")
    private String contactTel;
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "유효한 이메일 형식이 아닙니다.")
    private String contactEmail;

    private String note;
}
