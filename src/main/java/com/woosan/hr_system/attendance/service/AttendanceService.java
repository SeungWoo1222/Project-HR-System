package com.woosan.hr_system.attendance.service;

import com.woosan.hr_system.attendance.model.Attendance;

import java.util.List;

public interface AttendanceService {
    Attendance getAttendanceById(int attendanceId);
    List<Attendance> getAttendanceByEmployeeId(String employeeId);
    List<Attendance> getAllAttendance();
    List<Attendance> getTodayAttendance();
    List<Attendance> searchAttendance();
    List<Attendance> searchDeptAttendance(String department);
    Attendance hasTodayAttendanceRecord ();
    String checkIn();
    String checkOut();
    String earlyLeave(String notes);
    String editAttendance(Attendance attendance);
}
