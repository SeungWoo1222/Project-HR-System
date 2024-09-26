package com.woosan.hr_system.attendance.model;

import com.woosan.hr_system.employee.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Overtime {
    private int overtimeId;             // PK
    private String employeeId;          // FK
    private LocalDate date;             // 날짜
    private LocalTime startTime;        // 시작 시간
    private LocalTime endTime;          // 종료 시간
    private Float nightHours;           // 야간 근무 시간
    private Float totalHours;           // 총 초과근무 시간

    private Employee employee;          // 사원 정보
}
