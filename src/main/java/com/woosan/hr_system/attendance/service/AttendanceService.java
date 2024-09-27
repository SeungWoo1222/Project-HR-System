package com.woosan.hr_system.attendance.service;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    Attendance getAttendanceById(int attendanceId);
    List<Attendance> getAttendanceByEmployeeId(String employeeId);
    List<Attendance> getAllAttendance();
    List<Attendance> getTodayAttendance();
    PageResult<Attendance> searchAttendance(PageRequest pageRequest, String department, String status, YearMonth yearMonth);
    float getTotalWeeklyWorkingTime(String employeeId, LocalDate date);
    Map<String, Object> getThisMonthAttendance(String employeeId, YearMonth yearMonth);
    Attendance hasTodayAttendanceRecord ();
    String checkIn();
    String checkOut();
    String earlyLeave(String notes);
    String editAttendance(Attendance attendance);
}
