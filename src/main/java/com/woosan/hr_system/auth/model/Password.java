package com.woosan.hr_system.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Password {
    private String employeeId;
    private String password;
    private int passwordCount;
    private LocalDateTime lastModified;
    private String modifiedBy;
    private int strength;

    // 첫 비밀번호 생성 (생년월일 6자리)
    public Password(String employeeId, String birth) {
        this.employeeId = employeeId;
        this.password = birth;
    }

    // 비밀번호 변경 시 비밀번호 정보를 초기화
    public void initializePassword(String newPassword, LocalDateTime lastModified, String modifiedBy, int strength) {
        this.password = newPassword;
        this.lastModified = lastModified;
        this.modifiedBy = modifiedBy;
        this.strength = strength;
    }
}
