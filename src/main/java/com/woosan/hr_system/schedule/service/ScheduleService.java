package com.woosan.hr_system.schedule.service;

import com.woosan.hr_system.schedule.model.Schedule;

import java.util.List;

public interface ScheduleService {
    List<Schedule> getAllSchedules();
    List<Schedule> getSchedulesByEmployeeId(String employeeId);
    Schedule getScheduleById(int taskId);
    int insertSchedule(Schedule schedule);
    void updateSchedule(Schedule schedule);
    void updateScheduleStatus(int taskId, String status);
    void deleteSchedule(int taskId);

}
