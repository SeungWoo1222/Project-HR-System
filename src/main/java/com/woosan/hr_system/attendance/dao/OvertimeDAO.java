package com.woosan.hr_system.attendance.dao;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.attendance.model.Overtime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OvertimeDAO {
    Overtime getOvertimeById(int overtimeId);
    List<Overtime> getAllOvertimes();
    List<Overtime> getThisMonthOvertimes(String employeeId, YearMonth yearMonth);
    float getTotalWeeklyOvertime(String employeeId, LocalDate date);
    float getTotalWeeklyNightOvertime(String employeeId, LocalDate date);
    void insertOvertime(Overtime overtime, int attendanceId);
    void updateOvertime(Overtime overtime);
    void deleteOvertime(int overtimeId);
    List<Attendance> searchOvertime(Map<String, Object> params);
    int countOvertime(Map<String, Object> params);
}
