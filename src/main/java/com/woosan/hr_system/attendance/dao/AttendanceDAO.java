package com.woosan.hr_system.attendance.dao;

import com.woosan.hr_system.attendance.model.Attendance;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AttendanceDAO {
    Attendance getAttendanceById(int attendanceId);
    List<Attendance> getAttendanceByEmployeeId(String employeeId);
    List<Attendance> getAllAttendance();
    List<Attendance> getTodayAttendance();
    void insertAttendance(Attendance attendance);
    void updateCheckout(Map<String, Object> params);
    void updateEarlyLeave(Map<String, Object> params);
    void updateAttendance(Attendance updatedAttendance);
    int getMyTodayAttendance(Map<String, Object> params);
    List<Attendance> searchAttendance(Map<String, Object> params);
    int countAttendance(Map<String, Object> params);
    float getTotalWeeklyWorkingTime(String employeeId, LocalDate date);
    List<Attendance> getThisMonthAttendance(String employeeId, YearMonth yearMonth);
}
