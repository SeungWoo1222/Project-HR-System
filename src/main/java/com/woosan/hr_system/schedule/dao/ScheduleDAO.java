package com.woosan.hr_system.schedule.dao;

import com.woosan.hr_system.schedule.model.Schedule;
import java.util.List;

public interface ScheduleDAO {
    int insertSchedule(Schedule schedule);
    Schedule getScheduleById(int taskId);
    List<Schedule> getSchedulesByEmployeeId(String employeeId);
    List<Schedule> getAllSchedules();
    void updateSchedule(Schedule schedule);
    void updateScheduleStatus(int taskId, String status);
    void deleteSchedule(int taskId);
    void insertScheduleArchive(Schedule schedule);
}
