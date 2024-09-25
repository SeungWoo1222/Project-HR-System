package com.woosan.hr_system.attendance.service;

import com.woosan.hr_system.attendance.model.Overtime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OvertimeService {
    Overtime getOvertimeById(int overtimeId);
    List<Overtime> getAllOvertimes();
    Map<String, Object> getThisMonthOvertimes(String employeeId, YearMonth yearMonth);
    float getTotalWeeklyOvertime(String employeeId, LocalDate date);
    float getTotalWeeklyNightOvertime(String employeeId, LocalDate date);
    String addOvertime(int attendanceId, LocalDate date, LocalTime startTime, LocalTime endTime);
    String editOvertime(Overtime overtime);
    String deleteOvertime(int overtimeId);
}
